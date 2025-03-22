package com.definexjavaspringbootbootcamp.definexgraduationproject.service.implementations;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.AttachmentDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.AttachmentResponse;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.attachment.Attachment;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.AttachmentNotFoundException;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.TaskNotFoundException;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.AttachmentRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.AttachmentService;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TaskService taskService;

    private static final String UPLOAD_DIR = "/opt/uploads";
    @Override
    @PreAuthorize("@securityService.isUserAndTaskSameDepartment(#taskId)")
    public AttachmentResponse uploadAttachment(UUID taskId, MultipartFile file) {
        if (!taskService.doesTaskExist(taskId)){
            throw new TaskNotFoundException("Task not found");
        }
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Path.of(UPLOAD_DIR, fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Attachment attachment = Attachment.builder()
                    .filePath(filePath.toString())
                    .task(Task.builder().id(taskId).build())
                    .build();

            attachmentRepository.save(attachment);
            return AttachmentResponse.builder()
                    .filePath(filePath.toString())
                    .message("File uploaded successfully")
                    .build();
        }catch (IOException e){
            throw new RuntimeException("Error while uploading file", e);
        }
    }

    @Override
    @PreAuthorize("@securityService.isUserAndTaskSameDepartment(#taskId)")
    public List<AttachmentDto> getAttachmentsByTask(UUID taskId) {
        return attachmentRepository.findByTaskIdAndIsDeletedFalse(taskId).stream()
                .map(attachment -> AttachmentDto.builder()
                        .filePath(attachment.getFilePath())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public AttachmentResponse deleteAttachment(UUID attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException("Attachment not found"));
        attachment.setDeleted(true);
        attachmentRepository.save(attachment);
        return AttachmentResponse.builder()
                .message("Attachment marked as deleted")
                .filePath(attachment.getFilePath())
                .build();
    }

}