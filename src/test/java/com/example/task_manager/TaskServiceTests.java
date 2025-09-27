package com.example.task_manager;

import com.example.task_manager.dto.PageResponse;
import com.example.task_manager.dto.TaskCreateRequest;
import com.example.task_manager.dto.TaskPatchRequest;
import com.example.task_manager.dto.TaskResponse;
import com.example.task_manager.entity.TaskPriority;
import com.example.task_manager.entity.TaskStatus;
import com.example.task_manager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev") // H2; schema auto-created
class TaskServiceTests {

    @Autowired
    TaskService service;

    @Test
    void create_get_patch_list_flow() {
        // --- create (status/priority can be null to use defaults if your service sets them) ---
        TaskCreateRequest createReq = new TaskCreateRequest(
                "Test task",
                "desc",
                null,                     // status -> default OPEN
                TaskPriority.HIGH,        // or null to default MEDIUM
                "alice",
                "docs",
                Instant.now().plusSeconds(3600)
        );

        TaskResponse created = service.create(createReq);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Test task");
        assertThat(created.getStatus()).isIn(TaskStatus.OPEN, TaskStatus.IN_PROGRESS, TaskStatus.DONE);

        // --- get ---
        TaskResponse fetched = service.get(created.getId());
        assertThat(fetched.getId()).isEqualTo(created.getId());

        // --- patch (mark DONE) ---
        TaskPatchRequest patchReq = new TaskPatchRequest(
                null,               // title
                null,               // description
                TaskStatus.DONE,    // status
                null,               // priority
                null,               // assignee
                null,               // category
                null,               // dueDate
                created.getVersion()   // version for optimistic locking
        );
        TaskResponse afterPatch = service.patch(created.getId(), patchReq);
        assertThat(afterPatch.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(afterPatch.getCompletedAt()).isNotNull();

        // --- list with filters/paging/sort ---
        PageResponse<TaskResponse> page = service.list(
                "alice",
                "docs",
                TaskStatus.DONE,
                null,
                null,
                PageRequest.of(0, 10, Sort.by("createdAt"))
        );

        assertThat(page.getContent()).extracting(TaskResponse::getId).contains(afterPatch.getId());
        assertThat(page.getPage()).isEqualTo(0);
        assertThat(page.getSize()).isEqualTo(10);
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(1);
        assertThat(page.getTotalPages()).isGreaterThanOrEqualTo(1);
    }
}
