package com.definexjavaspringbootbootcamp.definexgraduationproject.controller;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.CreateProjectDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.ProjectDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.ProjectResponse;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/project")
@AllArgsConstructor
@PreAuthorize("hasAuthority('PROJECT_MANAGER')")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.findById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        return ResponseEntity.ok(projectService.findAllByDepartment());
    }

    @PostMapping("/create")
    public ResponseEntity<CreateProjectDto> createProject(@RequestBody CreateProjectDto projectDto) {
        return ResponseEntity.ok(projectService.create(projectDto));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable UUID id,@RequestBody CreateProjectDto projectDto) {
        return ResponseEntity.ok(projectService.update(id, projectDto));
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<ProjectDto> deleteProject(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.delete(id));
    }

    @PutMapping("/add-user/{projectId}")
    public ResponseEntity<ProjectResponse> addUserToProject(@PathVariable UUID projectId, @RequestBody List<UUID> userIds) {
        return ResponseEntity.ok(projectService.addUserToProject(projectId, userIds));
    }
}
