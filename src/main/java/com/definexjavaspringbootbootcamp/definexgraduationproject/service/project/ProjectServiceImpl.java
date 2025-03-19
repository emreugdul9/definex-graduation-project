package com.definexjavaspringbootbootcamp.definexgraduationproject.service.project;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project.Project;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.ProjectNotFoundException;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.project.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Override
    public Project findById(UUID id) {
        return projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public Project create(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public Project update(UUID id, Project project) {
        Project updatedProject = findById(project.getId());
        project.setId(updatedProject.getId());
        return projectRepository.save(project);
    }

    @Override
    public Project delete(UUID id) {
        Project deletedProject = findById(id);
        projectRepository.delete(deletedProject);
        return deletedProject;
    }
}