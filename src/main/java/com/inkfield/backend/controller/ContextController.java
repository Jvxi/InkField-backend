package com.inkfield.backend.controller;

import com.inkfield.backend.service.context.ContextService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/context")
public class ContextController {

    private final ContextService contextService;

    public ContextController(ContextService contextService) {
        this.contextService = contextService;
    }

    /**
     * 组装上下文包
     */
    @PostMapping("/{bookId}/chapter/{chapter}/assemble")
    public ResponseEntity<Map<String, Object>> assembleContext(
            @PathVariable String bookId,
            @PathVariable int chapter,
            @RequestParam(defaultValue = "write") String taskType
    ) {
        Map<String, Object> context = contextService.assembleContext(bookId, chapter, taskType);
        return ResponseEntity.ok(context);
    }

    /**
     * 获取写作指导
     */
    @GetMapping("/{bookId}/chapter/{chapter}/guidance")
    public ResponseEntity<Map<String, Object>> getWritingGuidance(
            @PathVariable String bookId,
            @PathVariable int chapter
    ) {
        Map<String, Object> guidance = contextService.getWritingGuidance(bookId, chapter);
        return ResponseEntity.ok(guidance);
    }
}
