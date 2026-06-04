package com.inkfield.backend.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;

import com.inkfield.backend.exception.ApiException;
import com.inkfield.backend.model.LibraryIndex;
import com.inkfield.backend.model.ProjectEnvelope;
import com.inkfield.backend.service.ProjectStore;

@RestController
@RequestMapping("/api/library")
public class LibraryController {
    private final ProjectStore projectStore;

    public LibraryController(ProjectStore projectStore) {
        this.projectStore = projectStore;
    }

    @GetMapping
    public LibraryIndex listLibrary() {
        return projectStore.loadLibrary();
    }

    @PostMapping("/books")
    public ProjectEnvelope createBook(@RequestBody(required = false) Map<String, String> payload) {
        String title = payload == null ? null : payload.get("title");
        String audienceChannel = payload == null ? null : payload.get("audienceChannel");
        String novelType = payload == null ? null : payload.get("novelType");
        if (audienceChannel == null || audienceChannel.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "请选择男频或女频。");
        }
        if (novelType == null || novelType.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "请选择小说类型。");
        }
        return projectStore.createBook(title, audienceChannel.trim(), novelType.trim());
    }

    @PutMapping("/active")
    public ProjectEnvelope switchActiveBook(@RequestBody Map<String, String> payload) {
        String bookId = payload == null ? null : payload.get("bookId");
        if (bookId == null || bookId.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "bookId 不能为空");
        }
        return projectStore.switchActiveBook(bookId.trim());
    }

    @DeleteMapping("/books/{bookId}")
    public LibraryIndex deleteBook(@PathVariable String bookId) {
        return projectStore.deleteBook(bookId);
    }
}
