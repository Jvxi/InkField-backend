package com.inkfield.backend.controller;

import com.inkfield.backend.model.memory.MemoryItem;
import com.inkfield.backend.model.memory.MemoryPack;
import com.inkfield.backend.service.memory.MemoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/memory")
public class MemoryController {

    private final MemoryService memoryService;

    public MemoryController(MemoryService memoryService) {
        this.memoryService = memoryService;
    }

    /**
     * 获取记忆包（写前注入）
     */
    @GetMapping("/{bookId}/pack")
    public ResponseEntity<MemoryPack> getMemoryPack(
            @PathVariable String bookId,
            @RequestParam(defaultValue = "0") int chapter,
            @RequestParam(defaultValue = "write") String taskType
    ) {
        MemoryPack pack = memoryService.buildMemoryPack(bookId, chapter, taskType);
        return ResponseEntity.ok(pack);
    }

    /**
     * 按类别查询记忆
     */
    @GetMapping("/{bookId}/category/{category}")
    public ResponseEntity<List<MemoryItem>> queryByCategory(
            @PathVariable String bookId,
            @PathVariable String category
    ) {
        List<MemoryItem> items = memoryService.queryMemory(bookId, category, null);
        return ResponseEntity.ok(items);
    }

    /**
     * 按主体查询记忆
     */
    @GetMapping("/{bookId}/subject/{subject}")
    public ResponseEntity<List<MemoryItem>> queryBySubject(
            @PathVariable String bookId,
            @PathVariable String subject
    ) {
        List<MemoryItem> items = memoryService.queryMemory(bookId, null, subject);
        return ResponseEntity.ok(items);
    }

    /**
     * 压缩记忆
     */
    @PostMapping("/{bookId}/compact")
    public ResponseEntity<Map<String, Object>> compactMemory(@PathVariable String bookId) {
        Map<String, Object> result = memoryService.compactMemory(bookId);
        return ResponseEntity.ok(result);
    }

    /**
     * 记忆统计
     */
    @GetMapping("/{bookId}/stats")
    public ResponseEntity<Map<String, Object>> getMemoryStats(@PathVariable String bookId) {
        Map<String, Object> stats = memoryService.getMemoryStats(bookId);
        return ResponseEntity.ok(stats);
    }
}
