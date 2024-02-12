package com.example.userApproval.dto;

import java.util.List;

public class TaskDto {
    private String title;
    private String description;
    private List<String> approvers;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getApprovers() {
        return approvers;
    }
}
