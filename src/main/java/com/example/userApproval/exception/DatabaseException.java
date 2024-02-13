package com.example.userApproval.exception;

import org.springframework.dao.DataAccessException;

public class DatabaseException extends DataAccessException {

    public DatabaseException(String msg) {
        super(msg);
    }
}
