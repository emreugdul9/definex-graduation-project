package com.definexjavaspringbootbootcamp.definexgraduationproject.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskUpdateDto {

    private String title;
    private String description;
    private String acceptanceCriteria;
}
