package com.definexjavaspringbootbootcamp.definexgraduationproject.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse extends Response {

    private String title;
    private String description;
    private String assignee;
    private LocalDate created;

}
