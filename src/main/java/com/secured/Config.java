package com.secured;

import com.secured.api.data.IUserDetailService;
import com.secured.api.decor.SecurityConfigAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The class configures web security using
 * @see SecurityConfigAPI
 * @Author Simon Cantor
 */
@EnableWebSecurity
@Configuration
public class Config extends WebSecurityConfigurerAdapter {
    @Autowired
    SecurityConfigAPI api;
    @Autowired
    IUserDetailService userDetailService;
    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {

        api.installCustomUserDetailService(authenticationManagerBuilder);

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        Map<String,Set<String>> mapRoles= new HashMap<>();
        Set<String> rolesAdmin= new HashSet<>();
        rolesAdmin.add("ROLE_ADMIN");
        mapRoles.put("/visit",rolesAdmin);
        Set<String> set = new HashSet<>();
        set.add("/login");
        api.configResourseProtection(http,mapRoles,set,null);

    }
}
