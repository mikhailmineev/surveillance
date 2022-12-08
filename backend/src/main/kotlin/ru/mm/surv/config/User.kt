package ru.mm.surv.config

import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
class User(
    val username: String,
    val password: String,
    val roles: List<UserRole>
)
