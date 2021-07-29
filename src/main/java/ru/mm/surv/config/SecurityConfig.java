package ru.mm.surv.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfig extends WebSecurityConfigurerAdapter {

        private final Users users;
        private final PasswordEncoder passwordEncoder;

        @Autowired
        public ApiWebSecurityConfig(Users users, PasswordEncoder passwordEncoder) {
            this.users = users;
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
            configureUsers(auth, users, passwordEncoder);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/stream/webm/publish/**")
                    .httpBasic()
                    .and()
                    .csrf()
                    .disable()
                    .authorizeRequests()
                    .antMatchers("/stream/webm/publish/**").hasRole(UserRole.PUBLISHER.toString());
        }
    }

    @Configuration
    @Order(2)
    public static class ApiTokenSecurityConfig extends WebSecurityConfigurerAdapter{

        private final Users users;
        private final PasswordEncoder passwordEncoder;

        @Autowired
        public ApiTokenSecurityConfig(Users users, PasswordEncoder passwordEncoder) {
            this.users = users;
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
            configureUsers(auth, users, passwordEncoder);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf()
                    .disable()
                    .authorizeRequests()
                    .antMatchers("/actuator/**").hasRole(UserRole.ADMIN.toString())
                    .antMatchers("/stream/webm/**").hasRole(UserRole.CONSUMER.toString())
                    .antMatchers("/stream/hls/**").hasRole(UserRole.CONSUMER.toString())
                    .antMatchers("/").hasRole(UserRole.CONSUMER.toString())
                    .anyRequest().permitAll()
                    .and()
                    .formLogin().defaultSuccessUrl("/", true).permitAll()
                    .and()
                    .logout().permitAll();
        }

    }

    private static void configureUsers(AuthenticationManagerBuilder auth, Users users, PasswordEncoder passwordEncoder) throws Exception {
        var inMemoryAuthentication = auth.inMemoryAuthentication();
        users.getUsers().forEach((k, v) ->
                inMemoryAuthentication
                        .withUser(v.getUsername())
                        .password(passwordEncoder.encode(v.getPassword()))
                        .roles(v.getRoles().stream().map(UserRole::toString).toArray(String[]::new)));
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}