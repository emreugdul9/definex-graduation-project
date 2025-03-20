package com.definexjavaspringbootbootcamp.definexgraduationproject.repository;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.attachment.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    List<Attachment> findByTaskIdAndIsDeletedFalse(UUID taskId);
}