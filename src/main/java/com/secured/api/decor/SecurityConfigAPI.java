package com.secured.api.decor;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.Map;
import java.util.Set;

/**
 * The interface declares functionality enabling hiden configuration
 * of security trailning mechanism including set up security manager with user details repository, installing
 * security filter, where authentication and impresonation processes are being done and configuration of
 * access to resources.
 * @Author Simon Cantor
 *
 */
public interface SecurityConfigAPI {
    class UNamePasswordDef
    {
        public boolean isAllowJustInRequestAuthentication() {
            return allowJustInRequestAuthentication;
        }

        public String getUserIdHeader() {
            return userIdHeader;
        }

        public String getUserPasswordHeader() {
            return userPasswordHeader;
        }

        private final boolean allowJustInRequestAuthentication;
        private final String  userIdHeader;
        private final String  userPasswordHeader;

        public UNamePasswordDef(boolean allowJustInRequestAuthentication, String userIdHeader, String userPasswordHeader) {
            this.allowJustInRequestAuthentication = allowJustInRequestAuthentication;
            this.userIdHeader = userIdHeader;
            this.userPasswordHeader = userPasswordHeader;
        }
    }
    void installCustomUserDetailService(AuthenticationManagerBuilder  builder) throws Exception;
    void installTokenControlFilter(HttpSecurity sec) throws Exception;
    void configResourseProtection(HttpSecurity http,Map<String,Set<String>> protectedResources,Set<String> openResources,UNamePasswordDef def) throws Exception;
}
