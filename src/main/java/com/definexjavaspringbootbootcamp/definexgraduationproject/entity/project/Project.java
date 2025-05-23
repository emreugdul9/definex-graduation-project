package com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String description;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_user",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks;
    @Enumerated(EnumType.STRING)
    private ProjectState projectState;
    private String departmentName;
    @JsonProperty("isDeleted")
    private boolean isDeleted;
    @CreatedDate
    @Column(updatable = false)
    private LocalDate created;
    @LastModifiedDate
    private LocalDate updated;

    @PrePersist
    public void prePersist() {
        this.isDeleted = false;
    }
}