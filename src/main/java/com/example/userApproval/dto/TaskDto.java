package com.example.userApproval.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class TaskDto {
    private String title;
    private String description;
    private List<String> approvers;
}
