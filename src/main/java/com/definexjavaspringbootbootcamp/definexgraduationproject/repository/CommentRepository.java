package com.definexjavaspringbootbootcamp.definexgraduationproject.repository;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    @Query(nativeQuery = true, value = "SELECT c.* FROM comment c WHERE c.task_id= :taskId")
    List<Comment> getCommentsByTaskId(@Param("taskId") UUID taskId);
}