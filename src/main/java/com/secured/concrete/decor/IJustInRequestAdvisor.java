package com.secured.concrete.decor;

import com.secured.api.decor.SecurityConfigAPI;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public interface IJustInRequestAdvisor {
    UserDetails adviceTokenNotFoundCase(HttpServletRequest req);
    void adviceSettings(SecurityConfigAPI.UNamePasswordDef def);
    void loginUsernamePassword(String userName, String password);
    void expectedResponse(HttpServletResponse response);
    void leaveRequestResponse();
    void bypassTokenFor(String r);
    boolean isToBypass(String s);
}
