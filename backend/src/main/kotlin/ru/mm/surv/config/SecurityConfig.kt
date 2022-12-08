package ru.mm.surv.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}

fun configureUsers(auth: AuthenticationManagerBuilder, users: Users, passwordEncoder: PasswordEncoder) {
    val inMemoryAuthentication = auth.inMemoryAuthentication()
    users.users.forEach { (_, v) ->
        inMemoryAuthentication
            .withUser(v.username)
            .password(passwordEncoder.encode(v.password))
            .roles(*v.roles.map(UserRole::toString).toTypedArray())
    }
}
