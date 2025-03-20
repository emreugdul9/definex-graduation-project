package com.definexjavaspringbootbootcamp.definexgraduationproject.service.project;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.ProjectDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.ProjectResponse;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project.Project;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    Project findById(UUID id);
    List<Project> findAll();
    Project create(ProjectDto projectDto);
    Project update(UUID id, ProjectDto projectDto);
    Project delete(UUID id);
    ProjectResponse addTaskToProject(UUID projectId, UUID taskId);
    ProjectResponse addUserToProject(UUID projectId, UUID userId);

}