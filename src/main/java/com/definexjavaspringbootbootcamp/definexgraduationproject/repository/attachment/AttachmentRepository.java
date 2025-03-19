package com.definexjavaspringbootbootcamp.definexgraduationproject.repository.attachment;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.attachment.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    List<Attachment> findByTaskIdAndIsDeletedFalse(UUID taskId);
}