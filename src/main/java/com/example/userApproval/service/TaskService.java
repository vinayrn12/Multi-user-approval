package com.example.userApproval.service;

import com.example.userApproval.dto.TaskDto;
import com.example.userApproval.entity.Task;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TaskService {
    void createTask(TaskDto taskDto, String author);

    List<Task> getTasks();
}
