package com.definexjavaspringbootbootcamp.definexgraduationproject.service;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.AttachmentDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.AttachmentResponse;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.attachment.Attachment;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.AttachmentNotFoundException;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.TaskNotFoundException;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.AttachmentRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.implementations.AttachmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    private UUID taskId;
    private UUID attachmentId;
    private Attachment attachment;
    private MockMultipartFile file;
    private Path mockPath;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        attachmentId = UUID.randomUUID();

        Task task = Task.builder()
                .id(taskId)
                .build();

        attachment = Attachment.builder()
                .id(attachmentId)
                .filePath("/opt/uploads/test_file.txt")
                .task(task)
                .isDeleted(false)
                .build();

        file = new MockMultipartFile(
                "file",
                "test_file.txt",
                "text/plain",
                "Test content".getBytes()
        );

        mockPath = Path.of("/opt/uploads/test_file.txt");
    }

    @Test
    void uploadAttachment_Success() throws IOException {
        when(taskService.doesTaskExist(taskId)).thenReturn(true);
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(attachment);

        try (MockedStatic<Path> mockedPath = mockStatic(Path.class);
             MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

            mockedPath.when(() -> Path.of(anyString(), anyString())).thenReturn(mockPath);
            mockedFiles.when(() -> Files.copy(any(InputStream.class), any(Path.class), any(StandardCopyOption.class)))
                    .thenReturn(0L);

            AttachmentResponse response = attachmentService.uploadAttachment(taskId, file);

            assertNotNull(response);
            assertEquals("File uploaded successfully", response.getMessage());
            assertTrue(response.getFilePath().contains("/opt/uploads"));

            verify(taskService).doesTaskExist(taskId);
            verify(attachmentRepository).save(any(Attachment.class));
        }
    }

    @Test
    void uploadAttachment_TaskNotFound() {
        when(taskService.doesTaskExist(taskId)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () ->
                attachmentService.uploadAttachment(taskId, file)
        );

        verify(taskService).doesTaskExist(taskId);
        verifyNoInteractions(attachmentRepository);
    }

    @Test
    void uploadAttachment_IOExceptionThrown() throws IOException {
        when(taskService.doesTaskExist(taskId)).thenReturn(true);

        try (MockedStatic<Path> mockedPath = mockStatic(Path.class);
             MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

            mockedPath.when(() -> Path.of(anyString(), anyString())).thenReturn(mockPath);
            mockedFiles.when(() -> Files.copy(any(InputStream.class), any(Path.class), any(StandardCopyOption.class)))
                    .thenThrow(new IOException("Simulated IO error"));

            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    attachmentService.uploadAttachment(taskId, file)
            );

            assertEquals("Error while uploading file", exception.getMessage());
            verify(taskService).doesTaskExist(taskId);
            verifyNoInteractions(attachmentRepository);
        }
    }

    @Test
    void getAttachmentsByTask_Success() {
        Attachment attachment1 = Attachment.builder()
                .id(UUID.randomUUID())
                .filePath("/opt/uploads/file1.txt")
                .isDeleted(false)
                .build();

        Attachment attachment2 = Attachment.builder()
                .id(UUID.randomUUID())
                .filePath("/opt/uploads/file2.txt")
                .isDeleted(false)
                .build();

        List<Attachment> attachments = Arrays.asList(attachment1, attachment2);

        when(attachmentRepository.findByTaskIdAndIsDeletedFalse(taskId)).thenReturn(attachments);

        List<AttachmentDto> result = attachmentService.getAttachmentsByTask(taskId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("/opt/uploads/file1.txt", result.get(0).getFilePath());
        assertEquals("/opt/uploads/file2.txt", result.get(1).getFilePath());

        verify(attachmentRepository).findByTaskIdAndIsDeletedFalse(taskId);
    }

    @Test
    void getAttachmentsByTask_EmptyList() {
        when(attachmentRepository.findByTaskIdAndIsDeletedFalse(taskId)).thenReturn(List.of());

        List<AttachmentDto> result = attachmentService.getAttachmentsByTask(taskId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(attachmentRepository).findByTaskIdAndIsDeletedFalse(taskId);
    }

    @Test
    void deleteAttachment_Success() {
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(attachment));
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(attachment);

        AttachmentResponse response = attachmentService.deleteAttachment(attachmentId);

        assertNotNull(response);
        assertEquals("Attachment marked as deleted", response.getMessage());
        assertEquals("/opt/uploads/test_file.txt", response.getFilePath());

        verify(attachmentRepository).findById(attachmentId);
        verify(attachmentRepository).save(attachment);
        assertTrue(attachment.isDeleted());
    }

    @Test
    void deleteAttachment_NotFound() {
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.empty());

        assertThrows(AttachmentNotFoundException.class, () ->
                attachmentService.deleteAttachment(attachmentId)
        );

        verify(attachmentRepository).findById(attachmentId);
        verifyNoMoreInteractions(attachmentRepository);
    }
}