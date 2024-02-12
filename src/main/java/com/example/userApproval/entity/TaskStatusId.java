package com.example.userApproval.entity;

import java.io.Serializable;
import java.util.Objects;


public class TaskStatusId implements Serializable {
    private String taskId;
    private String userId;

    public TaskStatusId() {
    }

    public TaskStatusId(String taskId, String userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskStatusId that = (TaskStatusId) o;
        return Objects.equals(taskId, that.taskId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, userId);
    }
}
