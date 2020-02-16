package com.secured.concrete.decor;

import com.secured.api.data.ICachedUserDetailAccess;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HandleAuthenticationFailed implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        String error="Authentification failure";
        if(e.getMessage()!=null)
            error=e.getMessage();
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,error);
    }
}
