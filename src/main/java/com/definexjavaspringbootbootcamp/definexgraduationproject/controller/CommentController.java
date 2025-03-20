package com.definexjavaspringbootbootcamp.definexgraduationproject.controller;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.CommentDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.CommentResponse;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comment")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/addComment/{id}")
    public ResponseEntity<CommentResponse> addComment(@PathVariable UUID id, @RequestBody CommentDto commentDto) {
        return ResponseEntity.ok(commentService.addComment(id, commentDto));
    }

    @GetMapping("/getAll/{id}")
    public ResponseEntity<List<CommentDto>> getAllComments(@PathVariable UUID id) {
        return ResponseEntity.ok(commentService.getCommentsByTaskId(id));
    }

}
