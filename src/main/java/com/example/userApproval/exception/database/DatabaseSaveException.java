package com.example.userApproval.exception.database;

import org.springframework.dao.DataAccessException;

public class DatabaseSaveException extends DataAccessException {
    public DatabaseSaveException(String msg) {
        super(msg);
    }
}
