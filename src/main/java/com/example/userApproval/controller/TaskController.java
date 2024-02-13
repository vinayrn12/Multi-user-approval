package com.example.userApproval.controller;

import com.example.userApproval.dto.TaskDto;
import com.example.userApproval.entity.Task;
import com.example.userApproval.exception.database.DatabaseSaveException;
import com.example.userApproval.exception.task.CannotApproveTaskException;
import com.example.userApproval.service.impl.TaskServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
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
    public ResponseEntity<String> createTask(@RequestBody TaskDto taskDto, Principal principal) {
        String author = principal.getName();
        try{
            taskService.createTask(taskDto, author);
        } catch (DataIntegrityViolationException ex){
            throw new DatabaseSaveException("Error creating task " + ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Task " + taskDto.getTitle() + " created successfully");
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity viewTasks(Authentication authentication) {
        List<Task> tasks = new ArrayList<>();
        try{
            tasks = taskService.getTasks();
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching tasks. Please try again");
        }
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @PostMapping("/{taskId}")
    public ResponseEntity<String> handleTaskAction(@PathVariable String taskId, @RequestParam("action") String action,
                                                   @RequestBody(required = false) String commentContent, Principal principal) {
        String username = principal.getName();
        if("Approve".equalsIgnoreCase(action)) {
            try {
                taskService.approveTask(taskId, username);
                return ResponseEntity.ok("Task approved successfully");
            } catch (Exception ex) {
                throw new CannotApproveTaskException("Error approving task: " + ex.getMessage());
            }
        } else if("Comment".equalsIgnoreCase(action)) {
            if(ObjectUtils.isEmpty(commentContent)) {
                return ResponseEntity.badRequest().body("Comment content is required");
            } else {
                taskService.addCommentToTask(taskId, commentContent, username);
                return ResponseEntity.ok("Comment added successfully");
            }
        } else {
            return ResponseEntity.badRequest().body("Action is invalid");
        }
    }
}
