package ru.mm.surv.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@Order(1)
class ApiWebSecurityConfig @Autowired constructor(
    private val users: Users,
    private val passwordEncoder: PasswordEncoder
) : WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder) {
        configureUsers(auth, users, passwordEncoder)
    }

    override fun configure(http: HttpSecurity) {
        http
            .antMatcher("/stream/webm/publish/**")
            .httpBasic()
            .and()
            .csrf()
            .disable()
            .authorizeRequests()
            .antMatchers("/stream/webm/publish/**").hasRole(UserRole.PUBLISHER.toString())
    }
}
