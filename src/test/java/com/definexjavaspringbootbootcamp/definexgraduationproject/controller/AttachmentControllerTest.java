package com.definexjavaspringbootbootcamp.definexgraduationproject.controller;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.AttachmentDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.AttachmentResponse;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.AttachmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AttachmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AttachmentService attachmentService;

    @InjectMocks
    private AttachmentController attachmentController;

    private UUID taskId;
    private UUID attachmentId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(attachmentController).build();

        taskId = UUID.randomUUID();
        attachmentId = UUID.randomUUID();
    }

    @Test
    void uploadAttachment_ShouldReturnSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Test file content".getBytes()
        );

        AttachmentResponse response = AttachmentResponse.builder()
                .filePath(System.getProperty("user.home") + "/uploads/some-uuid_test.txt")
                .message("File uploaded successfully")
                .build();

        when(attachmentService.uploadAttachment(eq(taskId), any(MockMultipartFile.class)))
                .thenReturn(response);

        mockMvc.perform(multipart("/api/attachment/upload/{id}", taskId)
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filePath").value(System.getProperty("user.home") + "/uploads/some-uuid_test.txt"))
                .andExpect(jsonPath("$.message").value("File uploaded successfully"));
    }

    @Test
    void getAllAttachments_ShouldReturnListOfAttachments() throws Exception {
        AttachmentDto attachment1 = AttachmentDto.builder()
                .filePath(System.getProperty("user.home") + "/uploads/file1.txt")
                .build();

        AttachmentDto attachment2 = AttachmentDto.builder()
                .filePath(System.getProperty("user.home") + "/uploads/file2.txt")
                .build();

        List<AttachmentDto> attachments = Arrays.asList(attachment1, attachment2);

        when(attachmentService.getAttachmentsByTask(taskId)).thenReturn(attachments);

        mockMvc.perform(get("/api/attachment/getAll/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filePath").value(System.getProperty("user.home") + "/uploads/file1.txt"))
                .andExpect(jsonPath("$[1].filePath").value(System.getProperty("user.home") + "/uploads/file2.txt"));
    }

    @Test
    void deleteAttachment_ShouldReturnSuccess() throws Exception {
        AttachmentResponse response = AttachmentResponse.builder()
                .filePath(System.getProperty("user.home") + "/uploads/file-to-delete.txt")
                .message("Attachment marked as deleted")
                .build();

        when(attachmentService.deleteAttachment(attachmentId)).thenReturn(response);

        mockMvc.perform(delete("/api/attachment/delete/{id}", attachmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filePath").value(System.getProperty("user.home") + "/uploads/file-to-delete.txt"))
                .andExpect(jsonPath("$.message").value("Attachment marked as deleted"));
    }
}