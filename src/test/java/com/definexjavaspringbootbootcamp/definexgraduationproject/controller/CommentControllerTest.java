package com.definexjavaspringbootbootcamp.definexgraduationproject.controller;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.CommentDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.CommentResponse;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UUID taskId;
    private UUID commentId;
    private CommentDto commentDto;
    private CommentResponse commentResponse;
    private List<CommentDto> commentDtoList;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
        objectMapper = new ObjectMapper();
        taskId = UUID.randomUUID();
        commentId = UUID.randomUUID();

        commentDto = CommentDto.builder()
                .taskId(taskId)
                .content("Test comment content")
                .build();

        commentResponse = CommentResponse.builder()
                .message("Comment added successfully")
                .commentId(commentId)
                .content("Test comment content")
                .taskId(taskId)
                .build();

        CommentDto commentDto1 = CommentDto.builder()
                .taskId(taskId)
                .content("First test comment")
                .build();

        CommentDto commentDto2 = CommentDto.builder()
                .taskId(taskId)
                .content("Second test comment")
                .build();

        commentDtoList = Arrays.asList(commentDto1, commentDto2);
    }

    @Test
    void addComment_Success() throws Exception {
        when(commentService.addComment(eq(taskId), any(CommentDto.class))).thenReturn(commentResponse);

        mockMvc.perform(post("/api/comment/addComment/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Comment added successfully")))
                .andExpect(jsonPath("$.commentId").value(commentId.toString()))
                .andExpect(jsonPath("$.content", is("Test comment content")))
                .andExpect(jsonPath("$.taskId").value(taskId.toString()));
    }

    @Test
    void getAllComments_Success() throws Exception {
        when(commentService.getCommentsByTaskId(taskId)).thenReturn(commentDtoList);

        mockMvc.perform(get("/api/comment/getAll/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].content", is("First test comment")))
                .andExpect(jsonPath("$[1].content", is("Second test comment")));
    }

    @Test
    void getAllComments_EmptyList() throws Exception {
        when(commentService.getCommentsByTaskId(taskId)).thenReturn(List.of());

        mockMvc.perform(get("/api/comment/getAll/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void addComment_InvalidJson() throws Exception {
        mockMvc.perform(post("/api/comment/addComment/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("\"INVALID\" \"JSON\"")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_EmptyComment() throws Exception {
        CommentDto emptyCommentDto = CommentDto.builder()
                .taskId(taskId)
                .content("")
                .build();

        CommentResponse emptyCommentResponse = CommentResponse.builder()
                .message("Empty comment added")
                .commentId(UUID.randomUUID())
                .content("")
                .taskId(taskId)
                .build();

        when(commentService.addComment(eq(taskId), any(CommentDto.class))).thenReturn(emptyCommentResponse);

        mockMvc.perform(post("/api/comment/addComment/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Empty comment added")))
                .andExpect(jsonPath("$.content", is("")));
    }
}