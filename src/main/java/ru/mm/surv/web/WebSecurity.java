package ru.mm.surv.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;

//@Configuration
//@EnableWebSecurity
public class WebSecurity {//extends WebSecurityConfigurerAdapter {
//
//    //@Override
//    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("publisher").password(passwordEncoder().encode("gdsfgertgdfgs")).roles("PUBLISHER")
//                .and()
//                .withUser("consumer").password(passwordEncoder().encode("jftjhgbvn")).roles("CONSUMER");
//    }
//
//    //@Override
//    protected void configure(final HttpSecurity http) throws Exception {
//        http
//                //.httpBasic()
//                //.and()
//                .csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/publish/**").permitAll()//.hasRole("PUBLISHER")
//                .antMatchers("/consume/**").permitAll()
//                .anyRequest().permitAll()
//                .and()
//                .formLogin().permitAll()
//                .and()
//                .logout().permitAll();
//    }
//
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
}