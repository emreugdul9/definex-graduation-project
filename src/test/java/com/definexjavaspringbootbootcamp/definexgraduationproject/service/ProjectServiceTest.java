package com.definexjavaspringbootbootcamp.definexgraduationproject.service;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.ProjectDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.ProjectResponse;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project.Project;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project.ProjectState;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.ProjectNotFoundException;
import com.definexjavaspringbootbootcamp.definexgraduationproject.mapper.ProjectMapper;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.ProjectRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.TaskRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.UserRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.implementations.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private UUID projectId;
    private Project project;
    private ProjectDto projectDto;
    private User user;
    private List<Task> tasks;
    private List<User> users;
    private final String TEST_USERNAME = "testuser";
    private final String TEST_DEPARTMENT = "IT";

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(TEST_USERNAME);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findDepartmentByUsername(TEST_USERNAME)).thenReturn(TEST_DEPARTMENT);

        project = Project.builder()
                .id(projectId)
                .title("Test Project")
                .description("Test Description")
                .projectState(ProjectState.IN_PROGRESS)
                .departmentName(TEST_DEPARTMENT)
                .created(LocalDate.now())
                .updated(LocalDate.now())
                .isDeleted(false)
                .build();

        projectDto = ProjectDto.builder()
                .department(TEST_DEPARTMENT)
                .description("Test Description")
                .projectState(ProjectState.IN_PROGRESS)
                .title("Test Project")
                .build();


        tasks = new ArrayList<>();
        users = new ArrayList<>();
        project.setTasks(tasks);
        project.setUsers(users);
    }

    @Test
    void findById_WhenProjectExists_ShouldReturnProject() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Project foundProject = projectService.findById(projectId);

        assertNotNull(foundProject);
        assertEquals(projectId, foundProject.getId());
        assertEquals("Test Project", foundProject.getTitle());
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void findById_WhenProjectDoesNotExist_ShouldThrowProjectNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.findById(projectId));
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void findAllByDepartment_ShouldReturnProjects() {
        List<Project> projectList = List.of(project);
        when(projectRepository.findAllByDepartmentName(TEST_DEPARTMENT)).thenReturn(projectList);

        List<Project> result = projectService.findAllByDepartment();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Project", result.getFirst().getTitle());
        verify(projectRepository, times(1)).findAllByDepartmentName(TEST_DEPARTMENT);
    }

    @Test
    void create_ShouldReturnCreatedProject() {
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project createdProject = projectService.create(projectDto);

        assertNotNull(createdProject);
        assertEquals("Test Project", createdProject.getTitle());
        assertEquals(TEST_DEPARTMENT, createdProject.getDepartmentName());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void addTaskToProject_ShouldReturnProjectResponse() {
        UUID taskId1 = UUID.randomUUID();
        UUID taskId2 = UUID.randomUUID();
        List<UUID> taskIds = List.of(taskId1, taskId2);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponse response = projectService.addTaskToProject(projectId, taskIds);

        assertNotNull(response);
        assertEquals("Task added successfully", response.getMessage());
        assertEquals(2, project.getTasks().size());
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void addTaskToProject_WhenProjectNotFound_ShouldThrowProjectNotFoundException() {
        List<UUID> taskIds = List.of(UUID.randomUUID());
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.addTaskToProject(projectId, taskIds));
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void addUserToProject_ShouldReturnProjectResponse() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        List<UUID> userIds = List.of(userId1, userId2);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponse response = projectService.addUserToProject(projectId, userIds);

        assertNotNull(response);
        assertEquals("User added successfully", response.getMessage());
        assertEquals(2, project.getUsers().size());
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void addUserToProject_WhenProjectNotFound_ShouldThrowProjectNotFoundException() {
        List<UUID> userIds = List.of(UUID.randomUUID());
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.addUserToProject(projectId, userIds));
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void update_ShouldReturnUpdatedProject() {
        ProjectDto updatedProjectDto = ProjectDto.builder()
                .title("Updated Project Title")
                .description("Updated Description")
                .projectState(ProjectState.COMPLETED)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        doAnswer(invocation -> {
            ProjectDto dto = invocation.getArgument(0);
            Project targetProject = invocation.getArgument(1);

            if (dto.getTitle() != null) targetProject.setTitle(dto.getTitle());
            if (dto.getDescription() != null) targetProject.setDescription(dto.getDescription());
            if (dto.getProjectState() != null) targetProject.setProjectState(dto.getProjectState());
            targetProject.setUpdated(LocalDate.now());

            return null;
        }).when(projectMapper).updateProjectFromDto(any(ProjectDto.class), any(Project.class));

        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));


        Project updatedProject = projectService.update(projectId, updatedProjectDto);

        assertNotNull(updatedProject);
        assertEquals("Updated Project Title", updatedProject.getTitle());
        assertEquals("Updated Description", updatedProject.getDescription());
        assertEquals(ProjectState.COMPLETED, updatedProject.getProjectState());
        assertEquals(TEST_DEPARTMENT, updatedProject.getDepartmentName());

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectMapper, times(1)).updateProjectFromDto(eq(updatedProjectDto), eq(project));
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void update_WithPartialData_ShouldOnlyUpdateProvidedFields() {

        ProjectDto partialUpdateDto = ProjectDto.builder()
                .title("Updated Title")
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        doAnswer(invocation -> {
            ProjectDto dto = invocation.getArgument(0);
            Project targetProject = invocation.getArgument(1);

            if (dto.getTitle() != null) targetProject.setTitle(dto.getTitle());
            targetProject.setUpdated(LocalDate.now());

            return null;
        }).when(projectMapper).updateProjectFromDto(any(ProjectDto.class), any(Project.class));

        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Project updatedProject = projectService.update(projectId, partialUpdateDto);

        assertNotNull(updatedProject);
        assertEquals("Updated Title", updatedProject.getTitle());
        assertEquals("Test Description", updatedProject.getDescription());
        assertEquals(ProjectState.IN_PROGRESS, updatedProject.getProjectState());

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectMapper, times(1)).updateProjectFromDto(eq(partialUpdateDto), eq(project));
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void update_WhenProjectNotFound_ShouldThrowProjectNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.update(projectId, projectDto));
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void delete_ShouldMarkProjectAsDeleted() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Project deletedProject = projectService.delete(projectId);

        assertTrue(deletedProject.isDeleted());
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void delete_WhenProjectNotFound_ShouldThrowProjectNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.delete(projectId));
        verify(projectRepository, times(1)).findById(projectId);
    }
}
