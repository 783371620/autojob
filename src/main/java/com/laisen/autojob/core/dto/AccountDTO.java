package com.laisen.autojob.core.dto;

import lombok.Data;

@Data
public class AccountDTO {
    private Long    id;
    private String  userId;
    private String  account;
    private String  password;
    private String  time;
    private String  type;
    private Integer hour = 0;
    private Integer mins = 0;

}
