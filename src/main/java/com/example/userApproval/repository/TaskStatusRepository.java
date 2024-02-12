package com.example.userApproval.repository;

import com.example.userApproval.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {
    @Query("SELECT COUNT(t) FROM TaskStatus t WHERE t.taskId = :taskId AND t.userId = :userId AND t.status = 'Approved'")
    Optional<Integer> approvalCountByUser(String taskId, String userId);

    @Query("SELECT COUNT(t) FROM TaskStatus t WHERE t.taskId = :taskId AND t.userId = :userId")
    Optional<Integer> taskApproverCount(String taskId, String userId);

    @Query("SELECT COUNT(t) FROM TaskStatus t WHERE t.taskId = :taskId AND t.status = 'Approved'")
    Optional<Integer> approvalCount(String taskId);

    @Modifying
    @Transactional
    @Query("UPDATE TaskStatus t set t.status = 'Approved' WHERE t.taskId = :taskId AND t.userId = :userId")
    void updateTaskStatus(String taskId, String userId);
}
