package ru.mm.surv.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@Order(2)
class ApiTokenSecurityConfig @Autowired constructor(
    private val users: Users,
    private val passwordEncoder: PasswordEncoder
) : WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder) {
        configureUsers(auth, users, passwordEncoder)
    }

    override fun configure(http: HttpSecurity) {
        http
            .csrf()
            .disable()
            .authorizeRequests()
            .antMatchers("/actuator/**").hasRole(UserRole.ADMIN.toString())
            .antMatchers("/configure/**").hasRole(UserRole.ADMIN.toString())
            .antMatchers("/stream/control/**").hasRole(UserRole.ADMIN.toString())
            .antMatchers("/stream/**").hasRole(UserRole.CONSUMER.toString())
            .antMatchers("/record/**").hasRole(UserRole.CONSUMER.toString())
            // TODO #22 remove after token auth implemented
            .antMatchers("/user").permitAll()
            .antMatchers("/system").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin().defaultSuccessUrl("/", true).permitAll()
            .and()
            .logout().permitAll()
    }
}
