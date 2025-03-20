package com.definexjavaspringbootbootcamp.definexgraduationproject.controller;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.AttachmentDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.AttachmentResponse;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.AttachmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attachment")
@AllArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping("/upload/{id}")
    public ResponseEntity<AttachmentResponse> uploadAttachment(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(attachmentService.uploadAttachment(id, file));
    }

    @GetMapping("/getAll/{id}")
    public ResponseEntity<List<AttachmentDto>> getAllAttachments(@PathVariable UUID id) {
        return ResponseEntity.ok(attachmentService.getAttachmentsByTask(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<AttachmentResponse> deleteAttachment(@PathVariable UUID id) {
        return ResponseEntity.ok(attachmentService.deleteAttachment(id));
    }
}
