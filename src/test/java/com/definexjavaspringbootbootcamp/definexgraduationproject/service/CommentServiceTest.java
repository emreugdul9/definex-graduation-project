package com.definexjavaspringbootbootcamp.definexgraduationproject.service;


import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.CommentDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.CommentResponse;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.comment.Comment;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskPriority;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskState;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.TaskNotFoundException;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.CommentRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.implementations.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private UUID taskId;
    private Comment comment;
    private CommentDto commentDto;
    private Task task;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();

        task = Task.builder()
                .id(taskId)
                .title("Test Task")
                .description("Test Description")
                .state(TaskState.BACKLOG)
                .acceptanceCriteria("test criteria")
                .assignee(User.builder().id(UUID.randomUUID()).build())
                .priority(TaskPriority.MEDIUM)
                .isDeleted(false)
                .build();

        commentDto = CommentDto.builder()
                .content("Test comment content")
                .taskId(taskId)
                .build();

        comment = Comment.builder()
                .id(UUID.randomUUID())
                .comment("Test comment content")
                .task(Task.builder().id(taskId).build())
                .build();
    }

    @Test
    void addComment_WhenTaskExists_ShouldAddCommentAndReturnResponse() {
        when(taskService.doesTaskExist(taskId)).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment savedComment = invocation.getArgument(0);
            savedComment.setId(UUID.randomUUID());
            return savedComment;
        });

        CommentResponse response = commentService.addComment(taskId, commentDto);

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertEquals("Comment successfully added", response.getMessage());
        assertEquals(commentDto.getContent(), response.getContent());
        assertNotNull(response.getTaskId());
        assertEquals(taskId, response.getTaskId());

        verify(taskService, times(1)).doesTaskExist(taskId);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_WhenTaskDoesNotExist_ShouldThrowTaskNotFoundException() {
        when(taskService.doesTaskExist(taskId)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> commentService.addComment(taskId, commentDto));

        verify(taskService, times(1)).doesTaskExist(taskId);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void getCommentsByTaskId_ShouldReturnListOfCommentDtos() {
        UUID commentId1 = UUID.randomUUID();
        UUID commentId2 = UUID.randomUUID();

        Comment comment1 = Comment.builder()
                .id(commentId1)
                .comment("Comment 1")
                .task(task)
                .build();

        Comment comment2 = Comment.builder()
                .id(commentId2)
                .comment("Comment 2")
                .task(task)
                .build();

        List<Comment> comments = Arrays.asList(comment1, comment2);

        when(commentRepository.getCommentsByTaskId(taskId)).thenReturn(comments);

        List<CommentDto> result = commentService.getCommentsByTaskId(taskId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Comment 1", result.get(0).getContent());
        assertEquals("Comment 2", result.get(1).getContent());
        assertEquals(taskId, result.get(0).getTaskId());
        assertEquals(taskId, result.get(1).getTaskId());

        verify(commentRepository, times(1)).getCommentsByTaskId(taskId);
    }

    @Test
    void getCommentsByTaskId_WhenNoComments_ShouldReturnEmptyList() {
        when(commentRepository.getCommentsByTaskId(taskId)).thenReturn(List.of());

        List<CommentDto> result = commentService.getCommentsByTaskId(taskId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(commentRepository, times(1)).getCommentsByTaskId(taskId);
    }

    @Test
    void convertToDto_ShouldConvertCommentToDto() {
        CommentDto result = commentService.convertToDto(comment);

        assertNotNull(result);
        assertEquals(comment.getComment(), result.getContent());
        assertEquals(comment.getTask().getId(), result.getTaskId());
    }
}