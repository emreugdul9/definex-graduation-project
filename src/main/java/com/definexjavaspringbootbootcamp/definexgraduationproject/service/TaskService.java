package com.definexjavaspringbootbootcamp.definexgraduationproject.service;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.*;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskPriority;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskState;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    FindTaskDto findById(UUID id);
    TaskResponse create(TaskDto taskDto);
    TaskResponse update(UUID id, TaskUpdateDto taskUpdateDto);
    String delete(UUID id);
    List<FindTaskDto> getTasksByProjectId(UUID projectId);
    ChangeStateResponse changeTaskState(UUID taskId, TaskState state, String reason);
    ChangePriortyResponse changeTaskPriority(UUID taskId, TaskPriority priority);
    TaskAssignedResponse assignTask(UUID taskId, UUID userId);
    TaskState getTaskState(UUID taskId);
    boolean doesTaskExist(UUID taskId);
}