package com.example.task_manager.entity;

import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tasks",
        indexes = {
                @Index(name="idx_tasks_assignee", columnList="assignee"),
                @Index(name="idx_tasks_category", columnList="category"),
                @Index(name="idx_tasks_status_due", columnList="status,due_date")
        })
public class Task {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable=false, length=200)
    private String title;

    @Column(columnDefinition="text")
    private String description;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20)
    private TaskStatus status = TaskStatus.OPEN;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Column(length=120)
    private String assignee;

    @Column(length=120)
    private String category;

    @Column(name="due_date")
    private Instant dueDate;

    @CreationTimestamp @Column(name="created_at", updatable=false)
    private Instant createdAt;

    @UpdateTimestamp @Column(name="updated_at")
    private Instant updatedAt;

    @Column(name="completed_at")
    private Instant completedAt;

    @Version
    private Long version;

    // getters/setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
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
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
