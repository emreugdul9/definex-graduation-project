package com.definexjavaspringbootbootcamp.definexgraduationproject.controller;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.*;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskPriority;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskState;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/task")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{id}")
    public ResponseEntity<FindTaskDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.findById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<TaskResponse> create(@RequestBody TaskDto taskDto) {
        return ResponseEntity.ok(taskService.create(taskDto));
    }

    @GetMapping("/exist/{id}")
    public ResponseEntity<Boolean> exist(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.doesTaskExist(id));
    }

    @PostMapping("/changeState/{id}")
    public ResponseEntity<ChangeStateResponse> changeState(@PathVariable UUID id, @RequestParam("state") TaskState state,@RequestBody String reason) {
        return ResponseEntity.ok(taskService.changeTaskState(id, state, reason));
    }

    @PostMapping("/changePriority/{id}")
    public ResponseEntity<ChangePriortyResponse> changePriority(@PathVariable UUID id, @RequestParam("priority") TaskPriority priority) {
        return ResponseEntity.ok(taskService.changeTaskPriority(id, priority));
    }


    @GetMapping("/state/{id}")
    public ResponseEntity<TaskState> findState(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getTaskState(id));
    }

    @PostMapping("/assign/{taskId}")
    public ResponseEntity<TaskAssignedResponse> assign(@PathVariable UUID taskId, @RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(taskService.assignTask(taskId, userId));
    }



}