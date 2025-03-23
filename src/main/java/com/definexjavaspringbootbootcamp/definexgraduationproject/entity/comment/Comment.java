package com.definexjavaspringbootbootcamp.definexgraduationproject.entity.comment;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String comment;

    @JoinColumn(name = "task_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;

    private Boolean isDeleted;

    @PrePersist
    public void prePersist() {
        this.isDeleted = false;
    }
}