package com.example.userApproval.service.impl;

import com.example.userApproval.dto.TaskDto;
import com.example.userApproval.entity.Comment;
import com.example.userApproval.entity.Task;
import com.example.userApproval.entity.TaskStatus;
import com.example.userApproval.exception.DatabaseException;
import com.example.userApproval.exception.TaskException;
import com.example.userApproval.repository.CommentRepository;
import com.example.userApproval.repository.TaskRepository;
import com.example.userApproval.repository.UserRepository;
import com.example.userApproval.service.TaskService;
import com.example.userApproval.service.TaskStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    private final TaskStatusService taskStatusService;
    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, CommentRepository commentRepository, TaskStatusService taskStatusService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.taskStatusService = taskStatusService;
    }

    @Override
    public void createTask(TaskDto taskDto, String author) {
        Task task = new Task();
        String taskId = UUID.randomUUID().toString();
        task.setTaskId(taskId).setTitle(taskDto.getTitle())
                .setDescription(taskDto.getDescription()).setAuthor(author).setStatus("New");
        if(!CollectionUtils.isEmpty(taskDto.getApprovers())) {
            for(String approver : taskDto.getApprovers()) {
                if(!approver.equalsIgnoreCase(author)) {
                    try{
                        String approverId = userRepository.findUserIdByEmail(approver).get();
                        taskStatusService.addTaskApprover(taskId, approverId);
                    } catch (NoSuchElementException ex) {
                        throw new DatabaseException("One of the given approvers is not listed with us.");
                    }
                }
            }
        }
        try {
            taskRepository.save(task);
        } catch (Exception ex) {
            throw new DatabaseException("Error saving details to database");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        try{
            tasks = taskRepository.findAll();
        } catch (Exception ex) {
            throw new DatabaseException("Error fetching tasks from database");
        }
        return tasks;
    }

    public ResponseEntity<String> handleTaskAction(String username, String taskId, String action, String commentContent) {
        if("Approve".equalsIgnoreCase(action)) {
            try {
                approveTask(taskId, username);
                return ResponseEntity.ok("Task approved successfully");
            } catch (Exception ex) {
                throw new TaskException("Error approving task: " + ex.getMessage());
            }
        } else if("Comment".equalsIgnoreCase(action)) {
            if(ObjectUtils.isEmpty(commentContent)) {
                return ResponseEntity.badRequest().body("Comment content is required");
            } else {
                addCommentToTask(taskId, commentContent, username);
                return ResponseEntity.ok("Comment added successfully");
            }
        } else {
            return ResponseEntity.badRequest().body("Action is invalid");
        }
    }

    @Transactional
    private void approveTask(String taskId, String userName) throws Exception {
        Task task = taskRepository.findTaskByTaskId(taskId).get();
        String userId = userRepository.findUserIdByEmail(userName).get();

        if(ObjectUtils.isEmpty(task)) {
            throw new TaskException("Task not found");
        }

        if(userName.equals(task.getAuthor())) {
            throw new TaskException("The task cannot be approved by the user who has created it");
        }

        if("Approved".equals(task.getStatus())) {
            throw new TaskException("Task is already approved");
        }

        handleMandatoryApprovers(taskId, userId);

        int approvalCountByUser = taskStatusService.getUserTaskStatus(taskId, userId, "Approved");

        if(approvalCountByUser > 0) {
            throw new TaskException("Task is already approved by the user");
        }

        if(taskStatusService.getTaskUserCount(taskId, userId) == 0) {
            TaskStatus taskStatus = new TaskStatus();
            taskStatus.setUserId(userId);
            taskStatus.setTaskId(taskId);
            taskStatus.setStatus("Approved");
            taskStatusService.saveTaskUserStatus(taskStatus);
        } else {
            taskStatusService.updateTaskStatus(taskId, userId);
        }

        int approvalCount = taskStatusService.getStatusCount(taskId, "Approved");

        if(approvalCount == 3) {
            task.setStatus("Approved");
            taskRepository.save(task);
        }

    }

    @Transactional
    private void addCommentToTask(String taskId, String commentContent, String author) {
        Task task;
        try{
            task = taskRepository.findTaskByTaskId(taskId).get();
        } catch(Exception ex) {
            throw new TaskException("Task with id: " + taskId + " not found");
        }

        Comment comment = new Comment();
        comment.setContent(commentContent);
        comment.setAuthor(author);
        comment.setTask(task);

        task.addComment(comment);

        taskRepository.save(task);
        commentRepository.save(comment);
    }

    private void handleMandatoryApprovers(String taskId, String userId) {
        int mandatoryApproversCount = taskStatusService.getStatusCount(taskId, "Pending");
        if(mandatoryApproversCount == 3) {
            //check if current user is in the list for approvers
            if(taskStatusService.getUserTaskStatus(taskId, userId, "Pending") == 0)
                throw new TaskException("The task already has three mandatory approvers.");
        } else if(mandatoryApproversCount == 2) {
            if(taskStatusService.getStatusCount(taskId, "Approved") == 1 && taskStatusService.getUserTaskStatus(taskId, userId, "Pending") == 0)
                throw new TaskException("The task already has two mandatory approvers.");
        } else if(mandatoryApproversCount == 1) {
            if(taskStatusService.getStatusCount(taskId, "Approved") == 2 && taskStatusService.getUserTaskStatus(taskId, userId, "Pending") == 0)
                throw new TaskException("The task already has a mandatory approver.");
        }
    }
}
