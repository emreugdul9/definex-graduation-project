package com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.department.Department;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String description;
    @ElementCollection
    private List<String> teamMembers;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Task> tasks;
    @Enumerated(EnumType.STRING)
    private ProjectState projectState;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
    private boolean isActive;
    private Date created;
    private Date updated;
}