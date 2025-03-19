package com.definexjavaspringbootbootcamp.definexgraduationproject.dto;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskPriority;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskState;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskDto {

    private String title;
    private String description;
    private String acceptanceCriteria;
    private TaskPriority priority;
    private TaskState state;
    private String assignee;


}
