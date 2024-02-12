package com.example.userApproval.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "task_status")
@IdClass(TaskStatusId.class)
public class TaskStatus {
    @Id
    @Column(name = "task_id")
    private String taskId;

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "status")
    private String status;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
