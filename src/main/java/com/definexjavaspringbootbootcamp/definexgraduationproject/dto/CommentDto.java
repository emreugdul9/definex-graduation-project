package com.definexjavaspringbootbootcamp.definexgraduationproject.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CommentDto {
    private UUID taskId;
    private String content;
}
