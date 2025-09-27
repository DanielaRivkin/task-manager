package com.example.task_manager.service;

import com.example.task_manager.dto.PageResponse;
import com.example.task_manager.dto.TaskCreateRequest;
import com.example.task_manager.dto.TaskPatchRequest;
import com.example.task_manager.dto.TaskResponse;
import com.example.task_manager.entity.TaskStatus;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.UUID;

public interface TaskService {
    TaskResponse create(TaskCreateRequest req);
    TaskResponse get(UUID id);
    TaskResponse patch(UUID id, TaskPatchRequest req);
    void delete(UUID id);

    PageResponse<TaskResponse> list(
            String assignee,
            String category,
            TaskStatus status,
            Instant dueAfter,
            Instant dueBefore,
            Pageable pageable
    );
}
