package com.spring.aws.security;

import com.spring.aws.Service.CustomerUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;


@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private final CustomerUserDetails customerUserDetails;

    @Autowired
    public SecurityConfig(CustomerUserDetails customerUserDetails) {
        this.customerUserDetails = customerUserDetails;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, "/sign_up").permitAll()
                .antMatchers("/time/**").hasRole("USER")
                .antMatchers("/user/register").hasRole("USER")
                .and()
                .httpBasic();
    }



}
