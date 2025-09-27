package com.example.task_manager.controller;

import com.example.task_manager.dto.PageResponse;
import com.example.task_manager.dto.TaskCreateRequest;
import com.example.task_manager.dto.TaskPatchRequest;
import com.example.task_manager.dto.TaskResponse;
import com.example.task_manager.entity.TaskStatus;
import com.example.task_manager.service.TaskService;
import javax.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskCreateRequest req) {
        TaskResponse created = service.create(req);
        return ResponseEntity.created(URI.create("/api/v1/tasks/" + created.getId())).body(created);
    }

    @GetMapping("/{id}")
    public TaskResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PatchMapping("/{id}")
    public TaskResponse patch(@PathVariable UUID id, @RequestBody TaskPatchRequest req) {
        return service.patch(id, req);
    }

    // /api/v1/tasks?assignee=&category=&status=&dueAfter=&dueBefore=&page=&size=&sort=createdAt,-dueDate
    @GetMapping
    public PageResponse<TaskResponse> list(
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Instant dueAfter,
            @RequestParam(required = false) Instant dueBefore,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort) {

        Sort sortObj = Sort.by(Arrays.stream(sort.split(","))
                .map(s -> s.startsWith("-")
                        ? Sort.Order.desc(s.substring(1))
                        : Sort.Order.asc(s))
                .collect(java.util.stream.Collectors.toList()));

        Pageable pageable = PageRequest.of(page, Math.min(size, 100), sortObj);
        return service.list(assignee, category, status, dueAfter, dueBefore, pageable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
