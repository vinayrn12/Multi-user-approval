package com.example.userApproval.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EmailDto {
    private String to;
    private String subject;
    private String text;
}
