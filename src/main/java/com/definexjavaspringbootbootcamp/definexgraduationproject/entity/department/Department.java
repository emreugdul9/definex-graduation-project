package com.definexjavaspringbootbootcamp.definexgraduationproject.entity.department;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project.Project;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String departmentName;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Project> projects;
}