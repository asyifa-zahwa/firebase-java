package com.example.cobafirebase.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String userName;
    private String email;
    private String password;
    private Integer loginAttempt;
    private Boolean isLocked;
    private Date lastLogin;
    private Date createdADate;
    private Date updatedDate;
}