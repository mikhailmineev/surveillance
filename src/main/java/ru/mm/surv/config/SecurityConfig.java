package ru.mm.surv.config;

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
    public static class ApiWebSecurityConfig extends WebSecurityConfigurerAdapter{

        @Override
        protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
            configureUsers(auth);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("stream/webm/publish/**")
                    .httpBasic()
                    .and()
                    .csrf()
                    .disable()
                    .authorizeRequests()
                    .antMatchers("stream/webm/publish/**").hasRole("PUBLISHER");
        }
    }

    @Configuration
    @Order(2)
    public static class ApiTokenSecurityConfig extends WebSecurityConfigurerAdapter{

        @Override
        protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
            configureUsers(auth);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf()
                    .disable()
                    .authorizeRequests()
                    .antMatchers("stream/webm/**").hasRole("CONSUMER")
                    .antMatchers("stream/hls/**").hasRole("CONSUMER")
                    .antMatchers("/").hasRole("CONSUMER")
                    .anyRequest().permitAll()
                    .and()
                    .formLogin().permitAll()
                    .and()
                    .logout().permitAll();
            ;
        }

    }

    private static void configureUsers(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("publisher").password(passwordEncoder().encode("gdsfgertgdfgs")).roles("PUBLISHER")
                .and()
                .withUser("consumer").password(passwordEncoder().encode("jftjhgbvn")).roles("CONSUMER");
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}