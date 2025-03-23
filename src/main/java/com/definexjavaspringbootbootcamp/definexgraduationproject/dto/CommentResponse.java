package com.definexjavaspringbootbootcamp.definexgraduationproject.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse extends Response {
    private UUID commentId;
    private String content;
    private UUID taskId;
}
