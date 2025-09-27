package com.example.task_manager.mapper;

import com.example.task_manager.dto.*;
import com.example.task_manager.entity.*;
import java.time.Instant;

public class TaskMapper {

    public static Task toEntity(TaskCreateRequest r) {
        Task t = new Task();
        t.setTitle(r.getTitle());
        t.setDescription(r.getDescription());
        t.setStatus(r.getStatus() != null ? r.getStatus() : TaskStatus.OPEN);
        t.setPriority(r.getPriority() != null ? r.getPriority() : TaskPriority.MEDIUM);
        t.setAssignee(r.getAssignee());
        t.setCategory(r.getCategory());
        t.setDueDate(r.getDueDate());
        return t;
    }

    public static void applyPatch(Task t, TaskPatchRequest r) {
        if (r.getTitle() != null) t.setTitle(r.getTitle());
        if (r.getDescription() != null) t.setDescription(r.getDescription());
        if (r.getStatus() != null) t.setStatus(r.getStatus());
        if (r.getPriority() != null) t.setPriority(r.getPriority());
        if (r.getAssignee() != null) t.setAssignee(r.getAssignee());
        if (r.getCategory() != null) t.setCategory(r.getCategory());
        if (r.getDueDate() != null) t.setDueDate(r.getDueDate());
        if (r.getVersion() != null) t.setVersion(r.getVersion());
    }

    public static void maybeSetCompletedAt(Task t) {
        if (t.getStatus() == TaskStatus.DONE && t.getCompletedAt() == null) t.setCompletedAt(Instant.now());
        if (t.getStatus() != TaskStatus.DONE) t.setCompletedAt(null);
    }

    public static TaskResponse toResponse(Task t) {
        return new TaskResponse(
                t.getId(), t.getTitle(), t.getDescription(), t.getStatus(), t.getPriority(),
                t.getAssignee(), t.getCategory(), t.getDueDate(),
                t.getCreatedAt(), t.getUpdatedAt(), t.getCompletedAt(), t.getVersion()
        );
    }
}
