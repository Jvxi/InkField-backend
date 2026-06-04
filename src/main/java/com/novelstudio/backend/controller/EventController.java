package com.novelstudio.backend.controller;

import com.novelstudio.backend.model.commit.StoryEvent;
import com.novelstudio.backend.service.story.EventAuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventAuditService eventAuditService;

    public EventController(EventAuditService eventAuditService) {
        this.eventAuditService = eventAuditService;
    }

    /**
     * 获取所有事件
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<List<StoryEvent>> getAllEvents(@PathVariable String bookId) {
        List<StoryEvent> events = eventAuditService.getAllEvents(bookId);
        return ResponseEntity.ok(events);
    }

    /**
     * 获取章节事件
     */
    @GetMapping("/{bookId}/chapter/{chapter}")
    public ResponseEntity<List<StoryEvent>> getChapterEvents(
            @PathVariable String bookId,
            @PathVariable int chapter
    ) {
        List<StoryEvent> events = eventAuditService.getChapterEvents(bookId, chapter);
        return ResponseEntity.ok(events);
    }

    /**
     * 获取实体相关事件
     */
    @GetMapping("/{bookId}/entity/{entityId}")
    public ResponseEntity<List<StoryEvent>> getEntityEvents(
            @PathVariable String bookId,
            @PathVariable String entityId
    ) {
        List<StoryEvent> events = eventAuditService.getEntityEvents(bookId, entityId);
        return ResponseEntity.ok(events);
    }

    /**
     * 按类型获取事件
     */
    @GetMapping("/{bookId}/type/{eventType}")
    public ResponseEntity<List<StoryEvent>> getEventsByType(
            @PathVariable String bookId,
            @PathVariable String eventType
    ) {
        List<StoryEvent> events = eventAuditService.getEventsByType(bookId, eventType);
        return ResponseEntity.ok(events);
    }
}
