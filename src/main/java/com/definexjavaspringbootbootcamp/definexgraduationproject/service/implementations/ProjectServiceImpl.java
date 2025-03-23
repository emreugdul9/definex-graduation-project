package com.definexjavaspringbootbootcamp.definexgraduationproject.service.implementations;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.CreateProjectDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.ProjectDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.ProjectResponse;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project.Project;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.ProjectNotFoundException;
import com.definexjavaspringbootbootcamp.definexgraduationproject.mapper.ProjectMapper;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.ProjectRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.UserRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.ProjectService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMapper = projectMapper;
    }

    @PreAuthorize("hasAuthority('PROJECT_MANAGER') and @securityService.isUserAndProjectSameDepartment(#id)")
    @Override
    public ProjectDto findById(UUID id) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        return getProjectDto(project);

    }

    @PreAuthorize("hasAuthority('PROJECT_MANAGER')")
    @Override
    public List<ProjectDto> findAllByDepartment() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String department = userRepository.findDepartmentByUsername(username);
        List<Project> project = projectRepository.findAllByDepartmentName(department);
        return project.stream()
                .map(this::getProjectDto).toList();
    }

    @PreAuthorize("hasAuthority('PROJECT_MANAGER')")
    @Override
    public CreateProjectDto create(CreateProjectDto projectDto) {
        Project project = Project.builder()
                .title(projectDto.getTitle())
                .description(projectDto.getDescription())
                .projectState(projectDto.getProjectState())
                .created(LocalDate.now())
                .updated(LocalDate.now())
                .build();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String department = userRepository.findDepartmentByUsername(username);
        project.setDepartmentName(department);
        projectRepository.save(project);
        return projectDto;
    }

    @PreAuthorize("hasAuthority('PROJECT_MANAGER') and @securityService.isUserAndProjectSameDepartment(#projectId)")
    @Override
    public ProjectResponse addUserToProject(UUID projectId, List<UUID> userIds) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        if (project.getUsers() == null) {
            project.setUsers(new ArrayList<>());
        }

        List<User> usersToAdd = userRepository.findAllById(userIds);
        for (User user : usersToAdd) {

            if (user.getProject() == null){
                user.setProject(new ArrayList<>());
            }

            if (!user.getProject().contains(project)){
                user.getProject().add(project);
            }

            if (!project.getUsers().contains(user)){
                project.getUsers().add(user);
            }
        }

        userRepository.saveAll(usersToAdd);
        projectRepository.save(project);
        return ProjectResponse.builder()
                .message("User added successfully")
                .users(project.getUsers().stream().map(User::getId).collect(Collectors.toList()))
                .tasks(project.getTasks().stream().map(Task::getId).collect(Collectors.toList()))
                .build();
    }

    @PreAuthorize("hasAuthority('PROJECT_MANAGER') and @securityService.isUserAndProjectSameDepartment(#id)")
    @Override
    public ProjectDto update(UUID id, CreateProjectDto projectDto) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        projectMapper.updateProjectFromDto(projectDto, project);
        project.setUpdated(LocalDate.now());
        projectRepository.save(project);

        return getProjectDto(project);

    }

    private ProjectDto getProjectDto(Project project) {
        return ProjectDto.builder()
                .title(project.getTitle())
                .description(project.getDescription())
                .projectState(project.getProjectState())
                .department(project.getDepartmentName())
                .tasks(project.getTasks().stream().map(Task::getId).collect(Collectors.toList()))
                .users(project.getUsers().stream().map(User::getId).collect(Collectors.toList()))
                .updated(String.valueOf(project.getUpdated()))
                .isDeleted(String.valueOf(project.isDeleted()))
                .build();
    }

    @PreAuthorize("hasAuthority('PROJECT_MANAGER') and @securityService.isUserAndProjectSameDepartment(#id)")
    @Override
    public ProjectDto delete(UUID id) {
        Project deletedProject = projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        deletedProject.setDeleted(true);
        return getProjectDto(deletedProject);
    }

}