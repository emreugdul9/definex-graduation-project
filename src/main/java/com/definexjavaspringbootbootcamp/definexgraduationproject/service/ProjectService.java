package com.definexjavaspringbootbootcamp.definexgraduationproject.service;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.CreateProjectDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.ProjectDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.ProjectResponse;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    ProjectDto findById(UUID id);
    List<ProjectDto> findAllByDepartment();
    CreateProjectDto create(CreateProjectDto projectDto);
    ProjectDto update(UUID id, CreateProjectDto projectDto);
    ProjectDto delete(UUID id);
    ProjectResponse addUserToProject(UUID projectId, List<UUID> userId);

}