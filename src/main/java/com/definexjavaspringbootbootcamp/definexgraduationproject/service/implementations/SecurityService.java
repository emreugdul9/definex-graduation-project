package com.definexjavaspringbootbootcamp.definexgraduationproject.service.implementations;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project.Project;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.user.User;
import com.definexjavaspringbootbootcamp.definexgraduationproject.exception.ProjectNotFoundException;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.ProjectRepository;
import com.definexjavaspringbootbootcamp.definexgraduationproject.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SecurityService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public SecurityService(UserRepository userRepository, ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    public boolean isUserInSameDepartment(UUID projectID) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Project project = projectRepository.findById(projectID)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));


        return currentUser.getDepartment().equals(project.getDepartment());
    }
}
