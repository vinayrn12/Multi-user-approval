package com.example.userApproval.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "task_status")
@IdClass(TaskStatusId.class)
@Getter
@Setter
public class TaskStatus {
    @Id
    @Column(name = "task_id")
    private String taskId;

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "status")
    private String status;
}
