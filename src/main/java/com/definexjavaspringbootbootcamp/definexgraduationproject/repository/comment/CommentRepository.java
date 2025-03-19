package com.definexjavaspringbootbootcamp.definexgraduationproject.repository.comment;

import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    @Query(nativeQuery = true, value = "SELECT c.* FROM comments c WHERE c.taskId= :taskId")
    List<Comment> getCommentsByTaskId(@Param("taskId") UUID taskId);
}