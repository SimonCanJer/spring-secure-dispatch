package com.secured.concrete.decor;


import com.secured.api.decor.ICredentialsToToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AuthenticationSuccessListener implements AuthenticationSuccessHandler {

    @Autowired
    ICredentialsToToken credentialsHandler;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {

        if(authentication instanceof UsernamePasswordAuthenticationToken)
        {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
            if(httpServletRequest.getHeader("Authentication")!=null)
            {
                return;
            }
            else
            {
                Set<String> set = new  HashSet<>();
                authentication.getAuthorities().stream().forEach(a->set.add(((GrantedAuthority) a).getAuthority()));
                credentialsHandler.trailUserCredentials((String)authentication.getPrincipal(),(String) authentication.getCredentials(), set);
            }


        }
    }
}
