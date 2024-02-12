package com.example.userApproval.exception.task;

public class TaskAlreadyApprovedException extends RuntimeException{
    public TaskAlreadyApprovedException(String message) {
        super(message);
    }
}
