package com.definexjavaspringbootbootcamp.definexgraduationproject.service.implementations;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.*;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskPriority;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskState;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.ReasonMustBeEntered;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.TaskNotFoundException;
import com.definexjavaspringbootbootcamp.definexgraduationproject.mapper.TaskMapper;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.TaskRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    @PreAuthorize("@securityService.isUserAndTaskSameDepartment(#id)")
    public Task findById(UUID id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found"));
    }

    @Override
    @Transactional
    @PreAuthorize("(hasAuthority('TEAM_LEADER') or hasAuthority('PROJECT_MANAGER'))")
    public TaskResponse create(TaskDto taskDto) {
        Task task = Task.builder()
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .acceptanceCriteria(taskDto.getAcceptanceCriteria())
                .state(TaskState.BACKLOG)
                .priority(taskDto.getPriority())
                .assignee(taskDto.getAssignee())
                .created(LocalDate.now())
                .updated(LocalDate.now())
                .build();
        taskRepository.save(task);
        return TaskResponse.builder()
                .assignee(task.getAssignee())
                .title(task.getTitle())
                .description(task.getDescription())
                .created(task.getCreated())
                .taskState(String.valueOf(task.getState()))
                .message("Task created successfully")
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("(hasAuthority('TEAM_LEADER') or hasAuthority('PROJECT_MANAGER')) and @securityService.isUserAndTaskSameDepartment(#id)")
    public Task update(UUID id, TaskUpdateDto taskUpdateDto) {
        Task taskToUpdate = findById(id);
        taskMapper.updateTaskFromDto(taskUpdateDto, taskToUpdate);
        return taskRepository.save(taskToUpdate);
    }

    @Override
    @Transactional
    @PreAuthorize("(hasAuthority('TEAM_LEADER') or hasAuthority('PROJECT_MANAGER')) and @securityService.isUserAndTaskSameDepartment(#id)")
    public String delete(UUID id) {
        Task taskToDelete = findById(id);
        taskToDelete.setDeleted(true);
        return "Task deleted successfully";
    }

    @Override
    @PreAuthorize("(hasAuthority('TEAM_LEADER') or hasAuthority('PROJECT_MANAGER')) and @securityService.isUserAndProjectSameDepartment(#projectId)")
    public List<Task> getTasksByProjectId(UUID projectId) {
        return taskRepository.findTasksByProjectId(projectId);
    }

    @Override
    @Transactional
    @PreAuthorize("@securityService.isUserAndTaskSameDepartment(#taskId)")
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
        taskRepository.save(task);
        return ChangeStateResponse.builder()
                .reason(reason)
                .message("State changed to " + state)
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("(hasAuthority('TEAM_LEADER') or hasAuthority('PROJECT_MANAGER')) and @securityService.isUserAndTaskSameDepartment(#taskId)")
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
    @PreAuthorize("(hasAuthority('TEAM_LEADER') or hasAuthority('PROJECT_MANAGER')) and @securityService.isUserAndTaskSameDepartment(#taskId)")
    public TaskAssignedResponse assignTask(UUID taskId, UUID userId) {
        Task task = findById(taskId);
        task.setAssignee(User.builder().id(userId).build());
        taskRepository.save(task);
        return TaskAssignedResponse.builder()
                .taskId(taskId)
                .userId(userId)
                .message("Assigned task" + taskId + " to user " + userId)
                .build();
    }

    @Override
    @PreAuthorize("(hasAuthority('TEAM_LEADER') or hasAuthority('PROJECT_MANAGER')) and @securityService.isUserAndTaskSameDepartment(#taskId)")
    public TaskState getTaskState(UUID taskId) {
        return taskRepository.findTaskStateById(taskId);
    }

    @Override
    @PreAuthorize("(hasAuthority('TEAM_LEADER') or hasAuthority('PROJECT_MANAGER')) and @securityService.isUserAndTaskSameDepartment(#taskId)")
    public boolean doesTaskExist(UUID taskId) {
        return taskRepository.existsById(taskId);
    }

}