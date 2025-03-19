package com.definexjavaspringbootbootcamp.definexgraduationproject.repository.task;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Query(nativeQuery = true, value = "SELECT t.* FROM task t WHERE t.projectId= :projectId")
    List<Task> findTasksByProjectId(@Param("projectId") UUID projectId);

}