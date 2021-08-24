package ru.mm.surv.config;

import lombok.Data;

import java.util.List;

@Data
public class User {
    private String username;
    private String password;
    private List<UserRole> roles;
}
