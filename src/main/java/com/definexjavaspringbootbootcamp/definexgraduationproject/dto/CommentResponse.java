package com.definexjavaspringbootbootcamp.definexgraduationproject.dto;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.comment.Comment;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse extends Response {
    private Comment comment;
}
