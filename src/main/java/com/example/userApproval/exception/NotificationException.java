package com.example.userApproval.exception;

import jakarta.mail.MessagingException;

public class NotificationException extends MessagingException {
    public NotificationException(String s) {
        super(s);
    }
}
