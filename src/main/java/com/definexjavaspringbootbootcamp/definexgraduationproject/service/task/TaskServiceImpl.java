package com.definexjavaspringbootbootcamp.definexgraduationproject.service.task;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.*;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskPriority;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskState;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.ReasonMustBeEntered;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.TaskNotFoundException;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.task.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public Task findById(UUID id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found"));
    }

    @Override
    @Transactional
    public TaskResponse create(TaskDto taskDto) {
        Task task = Task.builder()
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .acceptanceCriteria(taskDto.getAcceptanceCriteria())
                .state(taskDto.getState())
                .priority(taskDto.getPriority())
                .assignee(taskDto.getAssignee())
                .created(LocalDate.now())
                .updated(LocalDate.now())
                .build();
        taskRepository.save(task);
        TaskResponse response = TaskResponse.builder()
                .assignee(task.getAssignee())
                .title(task.getTitle())
                .description(task.getDescription())
                .created(task.getCreated())
                .build();
        response.setMessage("Task created successfully");
        return response;
    }

    @Override
    @Transactional
    public Task update(UUID id, Task task) {
        Task taskToUpdate = findById(id);
        task.setId(taskToUpdate.getId());
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task delete(UUID id) {
        Task taskToDelete = findById(id);
        taskRepository.delete(taskToDelete);
        return taskToDelete;
    }

    @Override
    public List<Task> getTasksByProjectId(UUID projectId) {
        return taskRepository.findTasksByProjectId(projectId);
    }

    @Override
    @Transactional
    public ChangeStateResponse changeTaskState(UUID taskId, TaskState state, String reason) {
        Task task = findById(taskId);
        if((state == TaskState.CANCELLED || state == TaskState.BLOCKED) && reason == null) {
            throw new ReasonMustBeEntered("Reason cannot be null");
        }
        if (task.getState() == TaskState.COMPLETED) {
            throw new RuntimeException("Task is already completed");
        }
        if (state == TaskState.COMPLETED && task.getState() != TaskState.IN_PROGRESS) {
            throw new IllegalStateException("Task must be in progress before completed");
        }
        if (state == TaskState.BLOCKED){
            if (task.getState() != TaskState.IN_ANALYSIS && task.getState() != TaskState.IN_PROGRESS) {
                throw new IllegalStateException("Only tasks in progress and in analysis can be blocked");
            }
        }
        task.setState(state);
        this.update(taskId,task);
        ChangeStateResponse changeStateResponse = ChangeStateResponse.builder()
                .reason(reason)
                .build();
        changeStateResponse.setMessage("State changed to " + state);
        return changeStateResponse;
    }

    @Override
    @Transactional
    public ChangePriortyResponse changeTaskPriority(UUID taskId, TaskPriority priority){
        Task task = findById(taskId);
        task.setPriority(priority);
        taskRepository.save(task);
        return ChangePriortyResponse.builder()
                .priority(priority)
                .message("Priority changed to " + priority)
                .build();
    }

    @Override
    @Transactional
    public TaskAssignedResponse assignTask(UUID taskId, UUID userId) {
        Task task = findById(taskId);
        task.setAssignee(userId.toString());
        this.update(taskId,task);
        TaskAssignedResponse taskAssignedResponse = TaskAssignedResponse.builder()
                .taskId(taskId)
                .userId(userId)
                .build();
        taskAssignedResponse.setMessage("Assigned task " + taskId + " to user " + userId);
        return taskAssignedResponse;
    }

    @Override
    public TaskState getTaskState(UUID taskId) {
        Task task = findById(taskId);
        return task.getState();
    }

    @Override
    public boolean doesTaskExist(UUID taskId) {
        return taskRepository.existsById(taskId);
    }

}