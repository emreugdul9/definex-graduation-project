package com.definexjavaspringbootbootcamp.definexgraduationproject.service.implementations;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.CommentDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.CommentResponse;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.comment.Comment;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.TaskNotFoundException;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.CommentRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.CommentService;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskService taskService;

    @Override
    public CommentResponse addComment(UUID taskId, CommentDto commentDto) {
        if (!taskService.doesTaskExist(taskId)) {
            throw new TaskNotFoundException("Task not found");
        }
        Task task = Task.builder().id(taskId).build();
        Comment comment = Comment.builder()
                .comment(commentDto.getContent())
                .task(task)
                .build();
        commentRepository.save(comment);
        return CommentResponse.builder()
                .comment(comment)
                .message("Comment successfully added")
                .build();
    }

    @Override
    public List<CommentDto> getCommentsByTaskId(UUID taskId) {
        List<Comment> comments = commentRepository.getCommentsByTaskId(taskId);
        return comments.stream().map(this::convertToDto).collect(Collectors.toList());

    }

    public CommentDto convertToDto(Comment comment) {
        return CommentDto.builder()
                .content(comment.getComment())
                .taskId(comment.getTask().getId())
                .build();
    }
}