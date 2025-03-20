package com.definexjavaspringbootbootcamp.definexgraduationproject.controller;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.ChangePriortyResponse;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.ChangeStateResponse;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.TaskDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.TaskResponse;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskPriority;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.TaskState;
import com.definexjavaspringbootbootcamp.definexgraduationproject.service.task.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/task")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.findById(id));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('TEAM_LEADER') or hasAuthority('PROJECT_MANAGER')")
    public ResponseEntity<TaskResponse> create(@RequestBody TaskDto taskDto) {
        return ResponseEntity.ok(taskService.create(taskDto));
    }

    @GetMapping("/exist/{id}")
    @PreAuthorize("hasAuthority('TEAM_LEADER')")
    public ResponseEntity<Boolean> exist(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.doesTaskExist(id));
    }

    @PostMapping("/changeState/{id}")
    public ResponseEntity<ChangeStateResponse> changeState(@PathVariable UUID id, @RequestParam("state") TaskState state,@RequestBody String reason) {
        return ResponseEntity.ok(taskService.changeTaskState(id, state, reason));
    }

    @PostMapping("/changePriority/{id}")
    @PreAuthorize("hasAuthority('TEAM_LEADER') or hasAuthority('PROJECT_MANAGER')")
    public ResponseEntity<ChangePriortyResponse> changePriority(@PathVariable UUID id, @RequestParam("priority") TaskPriority priority) {
        return ResponseEntity.ok(taskService.changeTaskPriority(id, priority));
    }


    @GetMapping("/state/{id}")
    @PreAuthorize("hasAuthority('TEAM_LEADER') or hasAuthority('PROJECT_MANAGER')")
    public ResponseEntity<TaskState> findState(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getTaskState(id));
    }




}