package ru.mm.surv.config;

import lombok.Data;

@Data
public class User {
    private String username;
    private String password;
    private UserRole role;
}
