package com.definexjavaspringbootbootcamp.definexgraduationproject.service.implementations;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.ProjectDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.ProjectResponse;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project.Project;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.ProjectNotFoundException;
import com.definexjavaspringbootbootcamp.definexgraduationproject.mapper.ProjectMapper;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.ProjectRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.TaskRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.UserRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.ProjectService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private final ProjectMapper projectMapper;
    private final TaskRepository taskRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, ProjectMapper projectMapper, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMapper = projectMapper;
        this.taskRepository = taskRepository;
    }

    //TODO: belki bu da sadece eğer aynı departmen içindeyse getiren bi metod olabilir
    @PreAuthorize("hasAuthority('PROJECT_MANAGER') and @securityService.isUserAndProjectSameDepartment(#id)")
    @Override
    public Project findById(UUID id) {
        return projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
    }

    @PreAuthorize("hasAuthority('PROJECT_MANAGER')")
    @Override
    public List<Project> findAllByDepartment() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String department = userRepository.findDepartmentByUsername(username);
        return projectRepository.findAllByDepartmentName(department);
    }

    @PreAuthorize("hasAuthority('PROJECT_MANAGER')")
    @Override
    public Project create(ProjectDto projectDto) {
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
        return projectRepository.save(project);
    }
    //TODO: addUserToProject, addTaskToProject

    @PreAuthorize("hasAuthority('PROJECT_MANAGER') and @securityService.isUserAndProjectSameDepartment(#projectId)")
    @Override
    public ProjectResponse addTaskToProject(UUID projectId, List<UUID> taskIds) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        List<Task> tasks = project.getTasks() != null ? project.getTasks() : new ArrayList<>();

        List<Task> tasksToAdd = taskRepository.findAllById(taskIds);
        for (Task task : tasksToAdd) {
            task.setProject(project);
            project.getTasks().add(task);
        }

        taskRepository.saveAll(tasks);
        return ProjectResponse.builder()
                .message("Task added successfully")
                .users(project.getUsers())
                .tasks(project.getTasks())
                .build();
    }

    @PreAuthorize("hasAuthority('PROJECT_MANAGER') and @securityService.isUserAndProjectSameDepartment(#projectId)")
    @Override
    public ProjectResponse addUserToProject(UUID projectId, List<UUID> userIds) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        List<User> users = project.getUsers() != null ? project.getUsers() : new ArrayList<>();

        List<User> usersToAdd = userRepository.findAllById(userIds);
        for (User user : usersToAdd) {
            user.getProject().add(project);
            project.getUsers().add(user);
        }

        userRepository.saveAll(users);
        return ProjectResponse.builder()
                .message("User added successfully")
                .users(project.getUsers())
                .tasks(project.getTasks())
                .build();
    }

    //TODO: düzelt
    @PreAuthorize("hasAuthority('PROJECT_MANAGER') and @securityService.isUserAndProjectSameDepartment(#id)")
    @Override
    public Project update(UUID id, ProjectDto projectDto) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        projectMapper.updateProjectFromDto(projectDto, project);
        return projectRepository.save(project);
    }

    @PreAuthorize("hasAuthority('PROJECT_MANAGER') and @securityService.isUserAndProjectSameDepartment(#id)")
    @Override
    public Project delete(UUID id) {
        Project deletedProject = projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        deletedProject.setDeleted(true);
        return deletedProject;
    }

}