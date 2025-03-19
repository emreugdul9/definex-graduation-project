package com.definexjavaspringbootbootcamp.definexgraduationproject.service.project;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project.Project;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    Project findById(UUID id);
    List<Project> findAll();
    Project create(Project project);
    Project update(UUID id, Project project);
    Project delete(UUID id);

}