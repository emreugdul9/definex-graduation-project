package com.definexjavaspringbootbootcamp.definexgraduationproject.service.comment;


import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.CommentDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.CommentResponse;

import java.util.List;
import java.util.UUID;

public interface CommentService {

    CommentResponse addComment(UUID taskId, CommentDto commentDto);
    List<CommentDto> getCommentsByTaskId(UUID taskId);

}