package com.definexjavaspringbootbootcamp.definexgraduationproject.dto;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskPriority;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TaskDto {

    private String title;
    private String description;
    private String acceptanceCriteria;
    private TaskPriority priority;
    private UUID project;

}
