package com.definexjavaspringbootbootcamp.definexgraduationproject.dto;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse extends Response {

    private String id;
    private String title;
    private String description;
    private String taskState;
    private String assignee;
    private LocalDate created;

}
