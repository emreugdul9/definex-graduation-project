package com.definexjavaspringbootbootcamp.definexgraduationproject.controller;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.CreateProjectDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.ProjectDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.ProjectResponse;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project.Project;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project.ProjectState;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.ProjectRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.TaskRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.UserRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.ProjectService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectController projectController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UUID projectId;
    private UUID userId1;
    private UUID userId2;
    private UUID taskId1;
    private UUID taskId2;
    private Project project;
    private CreateProjectDto createProjectDto;
    private ProjectDto projectDto;
    private ProjectDto projectDto2;
    private List<ProjectDto> projects;
    private List<UUID> userIds;
    private List<UUID> taskIds;
    private ProjectResponse projectResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        projectId = UUID.randomUUID();
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
        taskId1 = UUID.randomUUID();
        taskId2 = UUID.randomUUID();

        project = Project.builder()
                .id(projectId)
                .title("Test Project")
                .description("Test Project Description")
                .projectState(ProjectState.IN_PROGRESS)
                .departmentName("IT")
                .isDeleted(false)
                .build();

        createProjectDto = CreateProjectDto.builder()
                .title("Test Project")
                .description("Test Project Description")
                .projectState(ProjectState.IN_PROGRESS)
                .department("IT")
                .build();

        projectDto = ProjectDto.builder()
                .title("Test Project")
                .description("Test Project Description")
                .projectState(ProjectState.IN_PROGRESS)
                .department("IT")
                .updated(String.valueOf(LocalDate.now()))
                .tasks(Arrays.asList(taskId1, taskId2))
                .users(Arrays.asList(userId1, userId2))
                .build();

        projectDto2 = ProjectDto.builder()
                .title("Another Project")
                .description("Test Project Description")
                .projectState(ProjectState.IN_PROGRESS)
                .department("IT")
                .updated(String.valueOf(LocalDate.now()))
                .tasks(Arrays.asList(taskId1, taskId2))
                .users(Arrays.asList(userId1, userId2))
                .build();

        Project project2 = Project.builder()
                .id(UUID.randomUUID())
                .title("Another Project")
                .description("Another Project Description")
                .projectState(ProjectState.COMPLETED)
                .departmentName("IT")
                .isDeleted(false)
                .build();
        projects = Arrays.asList(projectDto, projectDto2);

        userIds = Arrays.asList(userId1, userId2);
        taskIds = Arrays.asList(taskId1, taskId2);

        projectResponse = ProjectResponse.builder()
                .message("Success")
                .users(userIds)
                .tasks(taskIds)
                .build();
    }

    @Test
    void getProject_ShouldReturnProject() throws Exception {
        when(projectService.findById(projectId)).thenReturn(projectDto);

        mockMvc.perform(get("/api/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Project"))
                .andExpect(jsonPath("$.description").value("Test Project Description"))
                .andExpect(jsonPath("$.projectState").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.department").value("IT"));

        verify(projectService, times(1)).findById(projectId);
    }

    @Test
    void getAllProjects_ShouldReturnAllProjects() throws Exception {
        when(projectService.findAllByDepartment()).thenReturn(projects);

        mockMvc.perform(get("/api/project/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Test Project"))
                .andExpect(jsonPath("$[1].title").value("Another Project"));

        verify(projectService, times(1)).findAllByDepartment();
    }

    @Test
    void createProject_ShouldReturnCreatedProject() throws Exception {
        when(projectService.create(any(CreateProjectDto.class))).thenReturn(createProjectDto);

        mockMvc.perform(post("/api/project/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProjectDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Project"))
                .andExpect(jsonPath("$.description").value("Test Project Description"))
                .andExpect(jsonPath("$.projectState").value("IN_PROGRESS"));

        verify(projectService, times(1)).create(any(CreateProjectDto.class));
    }

    @Test
    void updateProject_ShouldReturnUpdatedProject() throws Exception {
        when(projectService.update(eq(projectId), any(CreateProjectDto.class))).thenReturn(projectDto);

        mockMvc.perform(put("/api/project/update/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProjectDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Project"))
                .andExpect(jsonPath("$.description").value("Test Project Description"));

        verify(projectService, times(1)).update(eq(projectId), any(CreateProjectDto.class));
    }

    @Test
    void deleteProject_ShouldReturnDeletedProject() throws Exception {
        ProjectDto deletedProject = projectDto;
        deletedProject.setIsDeleted(String.valueOf(true));

        when(projectService.delete(projectId)).thenReturn(deletedProject);

        mockMvc.perform(delete("/api/project/delete/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isDeleted").value(String.valueOf(true)));

        verify(projectService, times(1)).delete(projectId);
    }

    @Test
    void addUserToProject_ShouldReturnProjectResponse() throws Exception {
        when(projectService.addUserToProject(eq(projectId), anyList())).thenReturn(projectResponse);

        mockMvc.perform(put("/api/project/add-user/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userIds)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users", hasSize(2)))
                .andExpect(jsonPath("$.tasks").isArray())
                .andExpect(jsonPath("$.tasks", hasSize(2)));

        verify(projectService, times(1)).addUserToProject(eq(projectId), anyList());
    }

}