package com.definexjavaspringbootbootcamp.definexgraduationproject.entity.attachment;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String filePath;
    private boolean isDeleted = Boolean.FALSE;

    @JoinColumn(name = "task_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;
}