package com.secured.concrete.decor;

import com.secured.api.IJwtTokenFactory;
import com.secured.api.JwtTokenDef;
import com.secured.api.data.ICachedUserDetailAccess;
import com.secured.api.data.IUserDetailService;
import com.secured.api.decor.ICredentialsToToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * The class implements the interface
 * @see ICredentialsToToken
 * The implementation just extracts user credentialzfrom HTTP request, generates token,
 * and keeps is as impresonation for the user request
 */
public class CredentialsToToken implements ICredentialsToToken {

    ThreadLocal<String> registeredToken= new ThreadLocal<>();
    @Qualifier(IUserDetailService.SYSTEM_QUALIFIER)
    @Autowired
    IUserDetailService userDetails;
    @Autowired
    @Qualifier(ICachedUserDetailAccess.SYSTEM_QUALIFIER)
    ICachedUserDetailAccess cache;
    @Autowired
    IJwtTokenFactory tokenManager;
    @Override
    public String trailUserCredentials(String uName, String password, Set<String> roles) {

        HttpServletRequest request =
                ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes())
                        .getRequest();

        JwtTokenDef def= new JwtTokenDef(uName,roles,request.getRemoteAddr());
        String str= tokenManager.generateSecuredToken(def);
        registeredToken.set(str);
        return str;
    }

    @Override
    public String pollToken() {
        try
        {
            return registeredToken.get();

        }
        finally
        {
            registeredToken.remove();

        }
    }
}
