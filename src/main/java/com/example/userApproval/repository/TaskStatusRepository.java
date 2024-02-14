package com.example.userApproval.repository;

import com.example.userApproval.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {
    @Query("SELECT COUNT(t) FROM TaskStatus t WHERE t.taskId = :taskId AND t.userId = :userId AND t.status = :status")
    Optional<Integer> taskUserStatus(String taskId, String userId, String status);

    @Query("SELECT COUNT(t) FROM TaskStatus t WHERE t.taskId = :taskId AND t.userId = :userId")
    Optional<Integer> taskApproverCount(String taskId, String userId);

    @Query("SELECT COUNT(t) FROM TaskStatus t WHERE t.taskId = :taskId AND t.status = :status")
    Optional<Integer> statusCount(String taskId, String status);

    @Modifying
    @Transactional
    @Query("UPDATE TaskStatus t set t.status = 'Approved' WHERE t.taskId = :taskId AND t.userId = :userId")
    void updateTaskStatus(String taskId, String userId);

    @Query("SELECT t.taskId FROM TaskStatus t WHERE t.userId= :userId AND t.status= 'Pending'")
    List<String> getPendingTasksByUserId(String userId);

    @Modifying
    @Query("DELETE FROM TaskStatus t WHERE t.taskId = :taskId AND t.userId = :userId AND t.status = 'Pending'")
    void deleteTaskApprover(String taskId, String userId);
}
