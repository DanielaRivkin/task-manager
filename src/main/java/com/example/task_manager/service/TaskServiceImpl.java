package com.example.task_manager.service;

import com.example.task_manager.dto.PageResponse;
import com.example.task_manager.dto.TaskCreateRequest;
import com.example.task_manager.dto.TaskPatchRequest;
import com.example.task_manager.dto.TaskResponse;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.TaskPriority;
import com.example.task_manager.entity.TaskStatus;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.TaskSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repo;

    public TaskServiceImpl(TaskRepository repo) {
        this.repo = repo;
    }

    @Override
    public TaskResponse create(TaskCreateRequest req) {
        Task t = new Task();
        t.setTitle(req.getTitle());
        t.setDescription(req.getDescription());
        t.setAssignee(req.getAssignee());
        t.setCategory(req.getCategory());
        t.setDueDate(req.getDueDate());
        t.setStatus(req.getStatus() != null ? req.getStatus() : TaskStatus.OPEN);
        t.setPriority(req.getPriority() != null ? req.getPriority() : TaskPriority.MEDIUM);
        t = repo.save(t);
        return toDto(t);
    }

    @Override
    public TaskResponse get(UUID id) {
        Task t = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
        return toDto(t);
    }

    @Override
    public TaskResponse patch(UUID id, TaskPatchRequest req) {
        Task t = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
        if (req.getTitle() != null) t.setTitle(req.getTitle());
        if (req.getDescription() != null) t.setDescription(req.getDescription());
        if (req.getAssignee() != null) t.setAssignee(req.getAssignee());
        if (req.getCategory() != null) t.setCategory(req.getCategory());
        if (req.getDueDate() != null) t.setDueDate(req.getDueDate());
        if (req.getPriority() != null) t.setPriority(req.getPriority());
        if (req.getStatus() != null) {
            t.setStatus(req.getStatus());
            if (req.getStatus() == TaskStatus.DONE && t.getCompletedAt() == null) {
                t.setCompletedAt(Instant.now());
            }
        }
        // optimistic lock if you want: if (req.version()!=null) assert req.version().equals(t.getVersion())
        t = repo.save(t);
        return toDto(t);
    }

    @Override
    public void delete(UUID id) {
        repo.deleteById(id);
    }

    @Override
    public PageResponse<TaskResponse> list(
            String assignee, String category, TaskStatus status,
            Instant dueAfter, Instant dueBefore, Pageable pageable) {

        Page<Task> page = repo.findAll(
                TaskSpecifications.filter(assignee, category, status, dueAfter, dueBefore),
                pageable
        );

        return new PageResponse<>(
                page.map(this::toDto).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                pageable.getSort().stream().map(Object::toString).collect(java.util.stream.Collectors.toList())
        );
    }

    private TaskResponse toDto(Task t) {
        return new TaskResponse(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getStatus(),
                t.getPriority(),
                t.getAssignee(),
                t.getCategory(),
                t.getDueDate(),
                t.getCreatedAt(),
                t.getUpdatedAt(),
                t.getCompletedAt(),
                t.getVersion()
        );
    }
}
