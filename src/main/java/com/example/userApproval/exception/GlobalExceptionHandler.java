package com.example.userApproval.exception;

import com.example.userApproval.exception.database.DatabaseSaveException;
import com.example.userApproval.exception.task.TaskAlreadyApprovedException;
import com.example.userApproval.exception.task.TaskNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<String> handleTaskNotFoundException(TaskNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found: " + ex.getMessage());
    }

    @ExceptionHandler(TaskAlreadyApprovedException.class)
    public ResponseEntity<String> handleTaskAlreadyApprovedException(TaskAlreadyApprovedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task already approved: " + ex.getMessage());
    }

    @ExceptionHandler(DatabaseSaveException.class)
    public ResponseEntity<String> handleDatabaseSaveException(DatabaseSaveException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in saving details to database.");
    }
}
