package com.definexjavaspringbootbootcamp.definexgraduationproject.repository;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Query(nativeQuery = true, value = "SELECT t.* FROM task t WHERE t.projectId= :projectId")
    List<Task> findTasksByProjectId(@Param("projectId") UUID projectId);

    @Query(nativeQuery = true, value = "SELECT t.task_state FROM task t WHERE t.id= :taskId")
    TaskState findTaskStateById(@Param("taskId") UUID taskId);
}