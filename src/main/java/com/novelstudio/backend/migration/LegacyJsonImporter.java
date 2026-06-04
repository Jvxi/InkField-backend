package com.novelstudio.backend.migration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novelstudio.backend.model.BookSummary;
import com.novelstudio.backend.model.LibraryIndex;
import com.novelstudio.backend.model.Project;
import com.novelstudio.backend.model.UserAccount;
import com.novelstudio.backend.persistence.ProjectJsonMapper;
import com.novelstudio.backend.service.BookLibraryService;
import com.novelstudio.backend.service.UserStore;

@Component
@Order(100)
public class LegacyJsonImporter implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(LegacyJsonImporter.class);

    private final UserStore userStore;
    private final BookLibraryService bookLibraryService;
    private final ObjectMapper objectMapper;
    private final ProjectJsonMapper projectJsonMapper;
    private final Path dataDir;
    private final Path legacyUsersFile;
    private final boolean importEnabled;

    public LegacyJsonImporter(
        UserStore userStore,
        BookLibraryService bookLibraryService,
        ObjectMapper objectMapper,
        ProjectJsonMapper projectJsonMapper,
        @Value("${novel.storage.import-legacy-json:true}") boolean importEnabled
    ) {
        this.userStore = userStore;
        this.bookLibraryService = bookLibraryService;
        this.objectMapper = objectMapper;
        this.projectJsonMapper = projectJsonMapper;
        this.dataDir = Paths.get("data").toAbsolutePath().normalize();
        this.legacyUsersFile = dataDir.resolve("users.json");
        this.importEnabled = importEnabled;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!importEnabled || !userStore.isEmpty()) {
            return;
        }
        try {
            importUsersFile();
            importPerUserDirectories();
            log.info("旧版 JSON 数据导入完成（如存在）。");
        } catch (IOException exception) {
            log.warn("导入旧版 JSON 数据失败: {}", exception.getMessage());
        }
    }

    private void importUsersFile() throws IOException {
        if (Files.notExists(legacyUsersFile)) {
            return;
        }
        String raw = Files.readString(legacyUsersFile, StandardCharsets.UTF_8);
        UserAccount[] users = objectMapper.readValue(raw, UserAccount[].class);
        if (users == null) {
            return;
        }
        for (UserAccount user : users) {
            userStore.save(user);
        }
        log.info("已从 {} 导入 {} 个用户", legacyUsersFile, users.length);
    }

    private void importPerUserDirectories() throws IOException {
        Path usersRoot = dataDir.resolve("users");
        if (Files.notExists(usersRoot)) {
            importLegacySingleProject();
            return;
        }
        try (Stream<Path> userDirs = Files.list(usersRoot).filter(Files::isDirectory)) {
            for (Path userDir : userDirs.toList()) {
                String userId = userDir.getFileName().toString();
                if (userStore.findById(userId).isEmpty()) {
                    continue;
                }
                importUserLibrary(userId, userDir);
                bookLibraryService.ensureUserLibrary(userId);
            }
        }
    }

    private void importUserLibrary(String userId, Path userDir) throws IOException {
        Path libraryFile = userDir.resolve("library.json");
        Path booksDir = userDir.resolve("books");
        if (Files.notExists(libraryFile) || Files.notExists(booksDir)) {
            return;
        }
        String raw = Files.readString(libraryFile, StandardCharsets.UTF_8);
        LibraryIndex library = objectMapper.readValue(raw, LibraryIndex.class);
        String activeBookId = library.activeBookId() == null ? "" : library.activeBookId();
        List<BookSummary> summaries = library.books() == null ? List.of() : library.books();

        List<String> importedIds = new ArrayList<>();
        for (BookSummary summary : summaries) {
            Path projectFile = booksDir.resolve(summary.id()).resolve("project.json");
            if (Files.notExists(projectFile)) {
                continue;
            }
            Project project = projectJsonMapper.fromJson(Files.readString(projectFile, StandardCharsets.UTF_8));
            bookLibraryService.importBook(userId, summary.id(), project, false);
            importedIds.add(summary.id());
        }

        if (!importedIds.isEmpty()) {
            String active = importedIds.contains(activeBookId) ? activeBookId : importedIds.getFirst();
            bookLibraryService.setActiveBookForUser(userId, active);
        }
    }

    private void importLegacySingleProject() throws IOException {
        Path legacyProject = dataDir.resolve("project.json");
        if (Files.notExists(legacyProject) || !userStore.isEmpty()) {
            return;
        }
        // 无用户时不自动创建；仅在有 users.json 时才有意义
    }
}
