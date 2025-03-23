package com.definexjavaspringbootbootcamp.definexgraduationproject.dto;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project.ProjectState;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;


@Data
@Builder
public class ProjectDto {

    private String id;
    private String title;
    private String description;
    private ProjectState projectState;
    private String department;
    private List<UUID> users;
    private List<UUID> tasks;
    private String updated;
    @JsonProperty("isDeleted")
    private String isDeleted;
}
