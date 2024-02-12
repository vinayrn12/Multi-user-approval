package com.example.userApproval.controller;

import com.example.userApproval.dto.TaskDto;
import com.example.userApproval.entity.Task;
import com.example.userApproval.service.impl.TaskServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private TaskServiceImpl taskService;

    @Autowired
    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<String> createTask(@RequestBody TaskDto taskDto, Authentication authentication) {
        if(ObjectUtils.isEmpty(authentication) || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User needs to be logged in to perform this action");
        }

        String author = authentication.getPrincipal().toString();
        try{
            taskService.createTask(taskDto, author);
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating task. Please try again");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Task " + taskDto.getTitle() + " created successfully");
    }

    @GetMapping
    public ResponseEntity<?> viewTasks(Authentication authentication) {
        if(ObjectUtils.isEmpty(authentication) || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User needs to be logged in to perform this action");
        }
        List<Task> tasks = new ArrayList<>();
        try{
            tasks = taskService.getTasks();
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching tasks. Please try again");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(tasks);
    }

    @PostMapping("/{taskId}/approve")
    public ResponseEntity<String> approveTask(@PathVariable String taskId, Principal principal) {
        String username = principal.getName();
        try{
            taskService.approveTask(taskId, username);
            return ResponseEntity.ok("Task approved successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error approving task: " + ex.getMessage());
        }
    }
}
