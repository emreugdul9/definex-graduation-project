package com.definexjavaspringbootbootcamp.definexgraduationproject.dto;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskPriority;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskDto {

    private String title;
    private String description;
    private String acceptanceCriteria;
    private TaskPriority priority;
    private User assignee;


}
