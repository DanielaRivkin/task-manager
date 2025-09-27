package com.example.task_manager;

import com.example.task_manager.controller.TaskController;
import com.example.task_manager.dto.PageResponse;
import com.example.task_manager.dto.TaskCreateRequest;
import com.example.task_manager.dto.TaskResponse;
import com.example.task_manager.entity.TaskPriority;
import com.example.task_manager.entity.TaskStatus;
import com.example.task_manager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerSliceTests {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockBean TaskService service;

    @Test
    void create_validationError_whenTitleBlank() throws Exception {
        String body = "{ \"title\": \"   \" }";

        mvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_returns201_andBody() throws Exception {
        UUID id = UUID.randomUUID();
        TaskResponse resp = new TaskResponse(
                id, "ok", null, TaskStatus.OPEN, TaskPriority.MEDIUM,
                null, null, null, Instant.now(), null, null, 0L
        );

        Mockito.when(service.create(any(TaskCreateRequest.class))).thenReturn(resp);

        String body = "{ \"title\": \"ok\" }";

        mvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/tasks/" + id))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("ok"));
    }

    @Test
    void list_returnsPageEnvelope() throws Exception {
        UUID id = UUID.randomUUID();
        TaskResponse item = new TaskResponse(
                id, "t1", null, TaskStatus.OPEN, TaskPriority.MEDIUM,
                null, null, null, Instant.now(), null, null, 0L
        );

        PageResponse<TaskResponse> page = new PageResponse<>(
                Arrays.asList(item), 0, 10, 1L, 1, Arrays.asList("createdAt")
        );

        Mockito.when(service.list(any(), any(), any(), any(), any(), any()))
                .thenReturn(page);

        mvc.perform(get("/api/v1/tasks?page=0&size=10&sort=createdAt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(id.toString()));
    }
}
