package com.example.userApproval.service;

import com.example.userApproval.entity.TaskStatus;
import com.example.userApproval.exception.database.DatabaseSaveException;
import com.example.userApproval.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskStatusService {
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    public TaskStatusService(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    public void addTaskApprover(String taskId, String approverId) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setTaskId(taskId);
        taskStatus.setUserId(approverId);
        taskStatus.setStatus("Pending");
        try{
            taskStatusRepository.save(taskStatus);
        } catch (Exception ex) {
            throw new DatabaseSaveException("Error in saving details to database.");
        }
    }

    public int getUserTaskStatus(String taskId, String userId, String status) {
        return taskStatusRepository.taskUserStatus(taskId, userId, status).get();
    }

    public int getStatusCount(String taskId, String status) {
        return taskStatusRepository.statusCount(taskId, status).get();
    }

    public void updateTaskStatus(String taskId, String userId) {
        taskStatusRepository.updateTaskStatus(taskId, userId);
    }

    public int getTaskUserCount(String taskId, String userId) {
        return taskStatusRepository.taskApproverCount(taskId, userId).get();
    }

    public void saveTaskUserStatus(TaskStatus taskStatus) {
        taskStatusRepository.save(taskStatus);
    }
}
