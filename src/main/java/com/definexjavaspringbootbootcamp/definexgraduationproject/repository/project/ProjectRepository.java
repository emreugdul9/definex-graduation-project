package com.definexjavaspringbootbootcamp.definexgraduationproject.repository.project;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

}