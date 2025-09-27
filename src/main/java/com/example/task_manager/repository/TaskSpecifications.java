package com.example.task_manager.repository;

import com.example.task_manager.entity.Task;          // <-- make sure these two imports exist
import com.example.task_manager.entity.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public final class TaskSpecifications {

    private TaskSpecifications() {}

    /** Compose all optional filters. Null parts are ignored by Spring Data. */
    public static Specification<Task> filter(
            String assignee,
            String category,
            TaskStatus status,
            Instant dueAfter,
            Instant dueBefore
    ) {
        return Specification
                .where(hasAssignee(assignee))
                .and(hasCategory(category))
                .and(hasStatus(status))
                .and(dueAfter(dueAfter))
                .and(dueBefore(dueBefore));
    }

    public static Specification<Task> hasAssignee(String assignee) {
        return (root, query, cb) ->
                (assignee == null || assignee.trim().isEmpty()) ? null : cb.equal(root.get("assignee"), assignee);
    }

    public static Specification<Task> hasCategory(String category) {
        return (root, query, cb) ->
                (category == null || category.trim().isEmpty()) ? null : cb.equal(root.get("category"), category);
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) ->
                (status == null) ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Task> dueAfter(Instant after) {
        return (root, query, cb) ->
                (after == null) ? null : cb.greaterThanOrEqualTo(root.get("dueDate"), after);
    }

    public static Specification<Task> dueBefore(Instant before) {
        return (root, query, cb) ->
                (before == null) ? null : cb.lessThanOrEqualTo(root.get("dueDate"), before);
    }
}
