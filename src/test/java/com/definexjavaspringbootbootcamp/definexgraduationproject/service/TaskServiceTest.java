package com.definexjavaspringbootbootcamp.definexgraduationproject.service;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.*;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project.Project;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskPriority;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskState;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.ReasonMustBeEntered;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.TaskNotFoundException;
import com.definexjavaspringbootbootcamp.definexgraduationproject.mapper.TaskMapper;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.TaskRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.implementations.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private UUID taskId;
    private UUID userId;
    private UUID projectId;
    private Task task;
    private User user;
    private TaskDto taskDto;
    private TaskUpdateDto taskUpdateDto;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .username("testUser")
                .departmentName("IT")
                .build();

        Project project = Project.builder()
                .id(projectId)
                .title("Test Project")
                .departmentName("IT")
                .build();

        task = Task.builder()
                .id(taskId)
                .title("Test Task")
                .description("Test Description")
                .acceptanceCriteria("Test Acceptance Criteria")
                .state(TaskState.BACKLOG)
                .priority(TaskPriority.MEDIUM)
                .assignee(user)
                .project(project)
                .created(LocalDate.now())
                .updated(LocalDate.now())
                .build();

        taskDto = TaskDto.builder()
                .title("New Task")
                .description("New Description")
                .acceptanceCriteria("New Acceptance Criteria")
                .priority(TaskPriority.HIGH)
                .build();

        taskUpdateDto = TaskUpdateDto.builder()
                .title("Updated Task")
                .description("Updated Description")
                .acceptanceCriteria("Updated Acceptance Criteria")
                .build();
    }

    @Test
    void findById_ShouldReturnTask_WhenTaskExists() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Task result = taskService.findById(taskId);

        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("Test Task", result.getTitle());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void findById_ShouldThrowTaskNotFoundException_WhenTaskDoesNotExist() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
                () -> taskService.findById(taskId));

        assertEquals("Task not found", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void create_ShouldCreateTaskSuccessfully() {
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0);
            savedTask.setId(taskId);
            return savedTask;
        });

        TaskResponse response = taskService.create(taskDto);
        assertNotNull(response);
        assertEquals("New Task", response.getTitle());
        assertEquals("New Description", response.getDescription());
        assertEquals(String.valueOf(TaskState.BACKLOG), response.getTaskState());
        assertEquals("Task created successfully", response.getMessage());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void update_ShouldUpdateTaskSuccessfully() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        doAnswer(invocation -> {
            Task taskToUpdate = invocation.getArgument(1);
            taskToUpdate.setTitle(taskUpdateDto.getTitle());
            taskToUpdate.setDescription(taskUpdateDto.getDescription());
            taskToUpdate.setAcceptanceCriteria(taskUpdateDto.getAcceptanceCriteria());
            return null;
        }).when(taskMapper).updateTaskFromDto(eq(taskUpdateDto), any(Task.class));

        Task updatedTask = taskService.update(taskId, taskUpdateDto);

        assertNotNull(updatedTask);
        assertEquals("Updated Task", updatedTask.getTitle());
        assertEquals("Updated Description", updatedTask.getDescription());
        assertEquals("Updated Acceptance Criteria", updatedTask.getAcceptanceCriteria());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskMapper, times(1)).updateTaskFromDto(eq(taskUpdateDto), any(Task.class));
    }

    @Test
    void delete_ShouldSoftDeleteTaskSuccessfully() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        String result = taskService.delete(taskId);

        assertEquals("Task deleted successfully", result);
        assertTrue(task.isDeleted());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void getTasksByProjectId_ShouldReturnTasksList() {
        List<Task> tasks = Arrays.asList(task);
        when(taskRepository.findTasksByProjectId(projectId)).thenReturn(tasks);

        List<Task> result = taskService.getTasksByProjectId(projectId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(taskId, result.getFirst().getId());
        verify(taskRepository, times(1)).findTasksByProjectId(projectId);
    }

    @Test
    void changeTaskState_ShouldChangeTaskStateSuccessfully() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        task.setState(TaskState.IN_PROGRESS);

        ChangeStateResponse response = taskService.changeTaskState(taskId, TaskState.COMPLETED, null);

        assertNotNull(response);
        assertEquals("State changed to COMPLETED", response.getMessage());
        assertEquals(TaskState.COMPLETED, task.getState());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void changeTaskState_ShouldThrowException_WhenReasonIsNullForCancelled() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        ReasonMustBeEntered exception = assertThrows(ReasonMustBeEntered.class,
                () -> taskService.changeTaskState(taskId, TaskState.CANCELLED, null));

        assertEquals("Reason cannot be null", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void changeTaskState_ShouldThrowException_WhenReasonIsNullForBlocked() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        ReasonMustBeEntered exception = assertThrows(ReasonMustBeEntered.class,
                () -> taskService.changeTaskState(taskId, TaskState.BLOCKED, null));

        assertEquals("Reason cannot be null", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void changeTaskState_ShouldThrowException_WhenTaskIsAlreadyCompleted() {
        task.setState(TaskState.COMPLETED);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> taskService.changeTaskState(taskId, TaskState.IN_PROGRESS, null));

        assertEquals("Task is already completed", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void changeTaskState_ShouldThrowException_WhenCompletingTaskNotInProgress() {
        task.setState(TaskState.IN_ANALYSIS);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> taskService.changeTaskState(taskId, TaskState.COMPLETED, null));

        assertEquals("Task must be in progress before completed", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void changeTaskState_ShouldThrowException_WhenBlockingTaskNotInValidState() {
        task.setState(TaskState.BACKLOG);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> taskService.changeTaskState(taskId, TaskState.BLOCKED, "Reason"));

        assertEquals("Only tasks in progress and in analysis can be blocked", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void changeTaskPriority_ShouldChangeTaskPrioritySuccessfully() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        ChangePriortyResponse response = taskService.changeTaskPriority(taskId, TaskPriority.HIGH);

        assertNotNull(response);
        assertEquals(TaskPriority.HIGH, response.getPriority());
        assertEquals("Priority changed to HIGH", response.getMessage());
        assertEquals(TaskPriority.HIGH, task.getPriority());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void assignTask_ShouldAssignTaskToUserSuccessfully() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        TaskAssignedResponse response = taskService.assignTask(taskId, userId);

        assertNotNull(response);
        assertEquals(taskId, response.getTaskId());
        assertEquals(userId, response.getUserId());
        assertEquals("Assigned task" + taskId + " to user " + userId, response.getMessage());
        assertEquals(userId, task.getAssignee().getId());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void getTaskState_ShouldReturnCorrectTaskState() {
        when(taskRepository.findTaskStateById(taskId)).thenReturn(TaskState.IN_PROGRESS);

        TaskState result = taskService.getTaskState(taskId);

        assertEquals(TaskState.IN_PROGRESS, result);
        verify(taskRepository, times(1)).findTaskStateById(taskId);
    }

    @Test
    void doesTaskExist_ShouldReturnTrue_WhenTaskExists() {
        when(taskRepository.existsById(taskId)).thenReturn(true);

        boolean result = taskService.doesTaskExist(taskId);

        assertTrue(result);
        verify(taskRepository, times(1)).existsById(taskId);
    }

    @Test
    void doesTaskExist_ShouldReturnFalse_WhenTaskDoesNotExist() {
        when(taskRepository.existsById(taskId)).thenReturn(false);

        boolean result = taskService.doesTaskExist(taskId);

        assertFalse(result);
        verify(taskRepository, times(1)).existsById(taskId);
    }
}