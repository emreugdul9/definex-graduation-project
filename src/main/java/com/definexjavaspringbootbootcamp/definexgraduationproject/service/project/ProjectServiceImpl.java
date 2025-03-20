package com.definexjavaspringbootbootcamp.definexgraduationproject.service.project;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.ProjectDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.ProjectResponse;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project.Project;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.ProjectNotFoundException;
import com.definexjavaspringbootbootcamp.definexgraduationproject.mapper.ProjectMapper;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.project.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    @Override
    public Project findById(UUID id) {
        return projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public Project create(ProjectDto projectDto) {
        Project project = Project.builder()
                .title(projectDto.getTitle())
                .description(projectDto.getDescription())
                .projectState(projectDto.getProjectState())
                .department(projectDto.getDepartment())
                .created(LocalDate.now())
                .updated(LocalDate.now())
                .build();
        return projectRepository.save(project);
    }
    //TODO: addUserToProject, addTaskToProject

    @Override
    public ProjectResponse addTaskToProject(UUID projectId, UUID taskId) {
        Project project = findById(projectId);
        List<Task> tasks = project.getTasks();
        tasks.add(Task.builder().id(taskId).build());
        project.setTasks(tasks);
        projectRepository.save(project);
        return ProjectResponse.builder()
                .message("Task added successfully")
                .users(project.getUsers())
                .tasks(project.getTasks())
                .build();
    }

    @Override
    public ProjectResponse addUserToProject(UUID projectId, UUID userId) {
        Project project = findById(projectId);
        List<User> users = project.getUsers();
        users.add(User.builder().id(userId).build());
        project.setUsers(users);
        projectRepository.save(project);
        return ProjectResponse.builder()
                .message("User added successfully")
                .users(project.getUsers())
                .tasks(project.getTasks())
                .build();
    }


    @Override
    public Project update(UUID id, ProjectDto projectDto) {
        Project project = findById(id);
        projectMapper.updateProjectFromDto(projectDto, project);
        return projectRepository.save(project);
    }

    @Override
    public Project delete(UUID id) {
        Project deletedProject = findById(id);
        projectRepository.delete(deletedProject);
        return deletedProject;
    }

}