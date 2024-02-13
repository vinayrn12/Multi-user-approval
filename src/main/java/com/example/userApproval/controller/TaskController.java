package com.example.userApproval.controller;

import com.example.userApproval.dto.TaskDto;
import com.example.userApproval.entity.Task;
import com.example.userApproval.service.impl.TaskServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskServiceImpl taskService;

    @Autowired
    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<String> createTask(@RequestBody TaskDto taskDto, Principal principal) {
        taskService.createTask(taskDto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body("Task " + taskDto.getTitle() + " created successfully");
    }

    @GetMapping
    public ResponseEntity<List<Task>> viewTasks(Authentication authentication) {
        List<Task> tasks = taskService.getTasks();
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @PostMapping("/{taskId}")
    public ResponseEntity<String> handleTaskAction(@PathVariable String taskId, @RequestParam("action") String action,
                                                   @RequestBody(required = false) String commentContent, Principal principal) {
        return taskService.handleTaskAction(principal.getName(), taskId, action, commentContent);
    }
}
