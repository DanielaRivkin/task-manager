package com.example.task_manager.dto;

import com.example.task_manager.entity.TaskPriority;
import com.example.task_manager.entity.TaskStatus;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.time.Instant;

public class TaskCreateRequest {
    @NotBlank @Size(max = 200)
    private String title;
    @Size(max = 10_000)
    private String description;
    private TaskStatus status;        // may be null -> default in service
    private TaskPriority priority;    // may be null -> default in service
    private String assignee;
    private String category;
    private Instant dueDate;

    public TaskCreateRequest() {}

    public TaskCreateRequest(String title, String description, TaskStatus status, TaskPriority priority, String assignee, String category, Instant dueDate) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.assignee = assignee;
        this.category = category;
        this.dueDate = dueDate;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }

    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Instant getDueDate() { return dueDate; }
    public void setDueDate(Instant dueDate) { this.dueDate = dueDate; }
}
