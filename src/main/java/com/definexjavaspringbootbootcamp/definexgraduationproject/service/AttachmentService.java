package com.definexjavaspringbootbootcamp.definexgraduationproject.service;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.AttachmentDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.AttachmentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface AttachmentService {
    AttachmentResponse uploadAttachment(UUID taskId, MultipartFile file);
    List<AttachmentDto> getAttachmentsByTask(UUID taskId);
    AttachmentResponse deleteAttachment(UUID attachmentId);

}