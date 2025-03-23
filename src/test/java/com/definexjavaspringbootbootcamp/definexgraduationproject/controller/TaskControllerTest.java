package com.definexjavaspringbootbootcamp.definexgraduationproject.controller;


import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.*;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.*;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private Task task;
    private TaskDto taskDto;
    private FindTaskDto findTaskDto;
    private TaskResponse taskResponse;
    private UUID taskId;
    private ObjectMapper objectMapper;
    private UUID userId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
        taskId = UUID.randomUUID();
        userId = UUID.randomUUID();
        objectMapper = new ObjectMapper();

        taskDto = TaskDto.builder()
                .title("Test Task")
                .description("Task description")
                .acceptanceCriteria("Criteria")
                .priority(TaskPriority.MEDIUM)
                .project(UUID.randomUUID())
                .build();

        findTaskDto = FindTaskDto.builder()
                .user(String.valueOf(userId))
                .project(String.valueOf(UUID.randomUUID()))
                .state(TaskState.BACKLOG)
                .title("Test Task")
                .acceptanceCriteria("Criteria")
                .description("Task description")
                .priority(TaskPriority.MEDIUM)
                .build();

        task = Task.builder()
                .id(taskId)
                .title("Test Task")
                .description("Task description")
                .acceptanceCriteria("Criteria")
                .state(TaskState.BACKLOG)
                .priority(TaskPriority.MEDIUM)
                .assignee(User.builder().id(userId).build())
                .build();

        taskResponse = TaskResponse.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .taskState(task.getState().name())
                .assignee(String.valueOf(task.getAssignee().getId()))
                .created(task.getCreated())
                .message("Task created successfully")
                .build();
    }

    @Test
    void findById_ShouldReturnTask() throws Exception {
        when(taskService.findById(taskId)).thenReturn(findTaskDto);

        mockMvc.perform(get("/api/task/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void create_ShouldReturnTaskResponse() throws Exception {
        when(taskService.create(any(TaskDto.class))).thenReturn(taskResponse);

        mockMvc.perform(post("/api/task/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void exist_ShouldReturnBoolean() throws Exception {
        when(taskService.doesTaskExist(taskId)).thenReturn(true);

        mockMvc.perform(get("/api/task/exist/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void changeState_ShouldReturnChangeStateResponse() throws Exception {
        ChangeStateResponse response = ChangeStateResponse.builder()
                .message("State changed to IN_PROGRESS")
                .reason("Reason for change")
                .build();

        when(taskService.changeTaskState(any(UUID.class), any(TaskState.class), any(String.class))).thenReturn(response);

        mockMvc.perform(post("/api/task/changeState/{id}", taskId)
                        .param("state", "IN_PROGRESS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("Reason for change")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("State changed to IN_PROGRESS"));
    }

    @Test
    void changePriority_ShouldReturnChangePriorityResponse() throws Exception {
        ChangePriortyResponse response = ChangePriortyResponse.builder()
                .message("Priority changed to HIGH")
                .priority(TaskPriority.HIGH)
                .build();

        when(taskService.changeTaskPriority(any(UUID.class), any(TaskPriority.class))).thenReturn(response);

        mockMvc.perform(post("/api/task/changePriority/{id}", taskId)
                        .param("priority", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Priority changed to HIGH"));
    }

    @Test
    void findState_ShouldReturnTaskStateAndOk() throws Exception {

        when(taskService.getTaskState(any(UUID.class))).thenReturn(TaskState.COMPLETED);
        mockMvc.perform(get("/api/task/state/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TaskState.COMPLETED)));
    }

    @Test
    void assign_ShouldReturnTaskAssignedResponse() throws Exception {
        TaskAssignedResponse assignedResponse = TaskAssignedResponse.builder()
                .taskId(taskId)
                .userId(userId)
                .message("Assigned task" + taskId + " to user " + userId)
                .build();

        when(taskService.assignTask(taskId, userId)).thenReturn(assignedResponse);

        mockMvc.perform(post("/api/task/assign/{taskId}", taskId)
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.taskId").value(taskId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.message").value("Assigned task" + taskId + " to user " + userId));

        verify(taskService, times(1)).assignTask(taskId, userId);
    }
}
