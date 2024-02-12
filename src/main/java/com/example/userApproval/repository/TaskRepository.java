package com.example.userApproval.repository;

import com.example.userApproval.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE t.taskId = :taskId")
    Optional<Task> findTaskByTaskId(String taskId);
}
