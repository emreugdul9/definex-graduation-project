package com.definexjavaspringbootbootcamp.definexgraduationproject.dto;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project.ProjectState;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ProjectDto {

    private String title;
    private String description;
    private ProjectState projectState;
    private String department;


}
