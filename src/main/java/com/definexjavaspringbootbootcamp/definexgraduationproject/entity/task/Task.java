package com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.attachment.Attachment;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.comment.Comment;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project.Project;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task")
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String description;
    private String acceptanceCriteria;
    @Enumerated(EnumType.STRING)
    private TaskState state;
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User assignee;
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Comment> comments;
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Attachment> attachments;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    private boolean isDeleted = Boolean.FALSE;
    private LocalDate created;
    private LocalDate updated;

}