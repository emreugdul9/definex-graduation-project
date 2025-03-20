package com.definexjavaspringbootbootcamp.definexgraduationproject.service;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.*;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskPriority;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskState;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    Task findById(UUID id);
    TaskResponse create(TaskDto taskDto);
    Task update(UUID id, Task task);
    Task delete(UUID id);
    List<Task> getTasksByProjectId(UUID projectId);
    ChangeStateResponse changeTaskState(UUID taskId, TaskState state, String reason);
    ChangePriortyResponse changeTaskPriority(UUID taskId, TaskPriority priority);
    TaskAssignedResponse assignTask(UUID taskId, UUID userId);
    TaskState getTaskState(UUID taskId);
    boolean doesTaskExist(UUID taskId);
}