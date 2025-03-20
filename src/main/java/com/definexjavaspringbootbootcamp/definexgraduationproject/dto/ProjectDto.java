package com.definexjavaspringbootbootcamp.definexgraduationproject.dto;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.department.Department;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project.ProjectState;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProjectDto {

    private String title;
    private String description;
    private List<User> teamMembers;
    private List<Task> tasks;
    private ProjectState projectState;
    private Department department;


}
