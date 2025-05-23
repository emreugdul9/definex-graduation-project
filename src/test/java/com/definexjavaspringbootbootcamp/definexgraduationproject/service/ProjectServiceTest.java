package com.definexjavaspringbootbootcamp.definexgraduationproject.service;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.CreateProjectDto;
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
import java.util.*;

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
    private CreateProjectDto createProjectDto;
    private User user1;
    private User user2;
    private List<Task> tasks;
    private List<User> users;
    private List<UUID> userIds;
    private final String TEST_USERNAME = "testuser";
    private final String TEST_DEPARTMENT = "IT";

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(TEST_USERNAME);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findDepartmentByUsername(TEST_USERNAME)).thenReturn(TEST_DEPARTMENT);

        projectId = UUID.randomUUID();
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        user1 = User.builder()
                .id(userId1)
                .username("user1")
                .password("password")
                .departmentName("IT")
                .project(new ArrayList<>())
                .build();

        user2 = User.builder()
                .id(userId2)
                .username("user2")
                .password("password")
                .departmentName("IT")
                .project(new ArrayList<>())
                .build();

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

        createProjectDto = CreateProjectDto.builder()
                .department(TEST_DEPARTMENT)
                .description("Test Description")
                .projectState(ProjectState.IN_PROGRESS)
                .title("Test Project")
                .build();

        projectDto = ProjectDto.builder()
                .title("Test Project")
                .description("Test Description")
                .projectState(ProjectState.IN_PROGRESS)
                .department("IT")
                .updated(String.valueOf(LocalDate.now()))
                .users(Arrays.asList(userId1, userId2))
                .build();

        userIds = Arrays.asList(userId1, userId2);

        tasks = new ArrayList<>();
        users = new ArrayList<>();
        project.setTasks(tasks);
        project.setUsers(users);

    }

    @Test
    void findById_WhenProjectExists_ShouldReturnProject() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        ProjectDto foundProject = projectService.findById(projectId);

        assertNotNull(foundProject);
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

        List<ProjectDto> result = projectService.findAllByDepartment();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Project", result.getFirst().getTitle());
        verify(projectRepository, times(1)).findAllByDepartmentName(TEST_DEPARTMENT);
    }

    @Test
    void create_ShouldReturnCreatedProject() {
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        CreateProjectDto createdProject = projectService.create(createProjectDto);

        assertNotNull(createdProject);
        assertEquals("Test Project", createdProject.getTitle());
        assertEquals(TEST_DEPARTMENT, createdProject.getDepartment());
        verify(projectRepository, times(1)).save(any(Project.class));
    }



    @Test
    void addUserToProject_Success() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findAllById(userIds)).thenReturn(Arrays.asList(user1, user2));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponse response = projectService.addUserToProject(projectId, userIds);

        assertNotNull(response);
        assertEquals("User added successfully", response.getMessage());
        assertEquals(2, response.getUsers().size());

        verify(userRepository, times(1)).saveAll(Arrays.asList(user1, user2));
        verify(projectRepository, times(1)).save(project);

        assertTrue(user1.getProject().contains(project));
        assertTrue(user2.getProject().contains(project));
        assertTrue(project.getUsers().contains(user1));
        assertTrue(project.getUsers().contains(user2));
    }

    @Test
    void addUserToProject_ProjectNotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> {
            projectService.addUserToProject(projectId, userIds);
        });

        verify(userRepository, never()).saveAll(any());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void addUserToProject_WithNullUsersList() {
        project.setUsers(null);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findAllById(userIds)).thenReturn(Arrays.asList(user1, user2));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponse response = projectService.addUserToProject(projectId, userIds);

        assertNotNull(response);
        assertNotNull(project.getUsers());
        assertEquals(2, project.getUsers().size());
    }

    @Test
    void addUserToProject_WithNullUserProjectList() {
        user1.setProject(null); // Kullanıcının proje listesini null olarak ayarla(emin değilim düzeltilebilir)
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findAllById(userIds)).thenReturn(Arrays.asList(user1, user2));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponse response = projectService.addUserToProject(projectId, userIds);

        assertNotNull(response);
        assertNotNull(user1.getProject());
        assertTrue(user1.getProject().contains(project));
    }

    @Test
    void addUserToProject_WithDuplicateUsers() {
        project.getUsers().add(user1);
        user1.getProject().add(project);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findAllById(userIds)).thenReturn(Arrays.asList(user1, user2));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponse response = projectService.addUserToProject(projectId, userIds);

        assertNotNull(response);
        assertEquals(2, project.getUsers().size());

        int user1Count = 0;
        for (User user : project.getUsers()) {
            if (user.equals(user1)) {
                user1Count++;
            }
        }
        assertEquals(1, user1Count);
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
        CreateProjectDto updatedProjectDto = CreateProjectDto.builder()
                .title("Updated Project Title")
                .description("Updated Description")
                .projectState(ProjectState.COMPLETED)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.ofNullable(project));

        doAnswer(invocation -> {
            CreateProjectDto dto = invocation.getArgument(0);
            Project targetProject = invocation.getArgument(1);

            if (dto.getTitle() != null) targetProject.setTitle(dto.getTitle());
            if (dto.getDescription() != null) targetProject.setDescription(dto.getDescription());
            if (dto.getProjectState() != null) targetProject.setProjectState(dto.getProjectState());
            targetProject.setUpdated(LocalDate.now());

            return null;
        }).when(projectMapper).updateProjectFromDto(any(CreateProjectDto.class), any(Project.class));

        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));


        ProjectDto updatedProject = projectService.update(projectId, updatedProjectDto);

        assertNotNull(updatedProject);
        assertEquals("Updated Project Title", updatedProject.getTitle());
        assertEquals("Updated Description", updatedProject.getDescription());
        assertEquals(ProjectState.COMPLETED, updatedProject.getProjectState());
        assertEquals(TEST_DEPARTMENT, updatedProject.getDepartment());

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectMapper, times(1)).updateProjectFromDto(eq(updatedProjectDto), eq(project));
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void update_WithPartialData_ShouldOnlyUpdateProvidedFields() {

        CreateProjectDto partialUpdateDto = CreateProjectDto.builder()
                .title("Updated Title")
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        doAnswer(invocation -> {
            CreateProjectDto dto = invocation.getArgument(0);
            Project targetProject = invocation.getArgument(1);

            if (dto.getTitle() != null) targetProject.setTitle(dto.getTitle());
            targetProject.setUpdated(LocalDate.now());

            return null;
        }).when(projectMapper).updateProjectFromDto(any(CreateProjectDto.class), any(Project.class));

        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectDto updatedProject = projectService.update(projectId, partialUpdateDto);

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

        assertThrows(ProjectNotFoundException.class, () -> projectService.update(projectId, createProjectDto));
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void delete_ShouldMarkProjectAsDeleted() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        ProjectDto deletedProject = projectService.delete(projectId);

        assertTrue(Boolean.parseBoolean(deletedProject.getIsDeleted()));
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void delete_WhenProjectNotFound_ShouldThrowProjectNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.delete(projectId));
        verify(projectRepository, times(1)).findById(projectId);
    }
}
