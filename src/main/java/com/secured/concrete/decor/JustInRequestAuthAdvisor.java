package com.secured.concrete.decor;

import com.secured.api.data.IUserDetailService;
import com.secured.api.decor.BadCredentialsException;
import com.secured.api.decor.SecurityConfigAPI;
import com.secured.api.decor.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.secured.api.decor.ICredentialsToToken;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The class implements the interface
 * @see IJustInRequestAdvisor
 *and handles the cases of request when user name and password are send directly in the request, but authenitication token
 * is not found(not sent by user)
 * The class tries to extract username and password from the request and find user details, if a user with
 * the credentials is registered and the credentials match that is in the data.
 *
 */
public class JustInRequestAuthAdvisor implements IJustInRequestAdvisor {
    ThreadLocal<HttpServletResponse> responseHolder = new ThreadLocal<>();
    Set<String> setBypass = new HashSet<>();
    private SecurityConfigAPI.UNamePasswordDef definitions;
    @Autowired
    IUserDetailService userDetailService;

    @Autowired
    ICredentialsToToken tokenManageentService;
    @Value("${secured.authentication.password.header}")
    String passwordHeader;
    @Value("${secured.authentication.userId.header}")
    String userIdheader;
    @Value("${secured.authentication.allowJustInRequestAuthentication}")
    Boolean allowJustInRequestAuth;
    @Value("${secured.authentication.timeout_sec}")
    Long dur = 600L;
    @PostConstruct
    void init()
    {
        if(passwordHeader!=null && userIdheader!=null && allowJustInRequestAuth!=null)
            definitions= new SecurityConfigAPI.UNamePasswordDef(allowJustInRequestAuth,userIdheader,passwordHeader);

    }

    /**
     * Called from filter (or another place is dealing with http servlet request) to try find
     * user credentials in a HttpServletRequest
     * @param req
     * @return
     */
    @Override
    public UserDetails adviceTokenNotFoundCase(HttpServletRequest req) {
        if(definitions==null ||!definitions.isAllowJustInRequestAuthentication())
            return null;
        String uid= req.getHeader(definitions.getUserIdHeader());
        if(uid==null)
            return null;
        String pass= req.getHeader(definitions.getUserPasswordHeader());
        if(pass==null)
            return null;
        UserDetails ud= userDetailService.getUserDetailsByUserName(uid);
        if(uid==null)
            return null;
        if(pass!=null)
        {
            if(!ud.getPassword().equals(pass))
            {
                return null;
            }
        }
          return ud;
    }

    @Override
    public void adviceSettings(SecurityConfigAPI.UNamePasswordDef def) {
        if(def!=null)
            definitions = def;

    }

    /**
     * Implementation of this method called when performing authentication by username and password
     * searches for user details in cache and database, invokes token management service to get credentials
     * and
     * @param userName
     * @param password
     */
    @Override
    public void loginUsernamePassword(String userName, String password) {
        UserDetails ud= userDetailService.getUserDetailsByUserName(userName);
        if(ud==null)
            throw new UserNotFoundException();
       if(!ud.getPassword().equals(password))
       {
           throw new BadCredentialsException();
       }
       Set<String> set=ud.getAuthorities().stream().map(a->((GrantedAuthority) a).getAuthority()).collect(Collectors.toSet());
       String token=tokenManageentService.trailUserCredentials(ud.getUsername(),ud.getPassword(),set);
       userDetailService.updateUseDetails(ud,token,dur*1000);
       HttpServletResponse response = responseHolder.get();
       if(response!=null)
           response.setHeader("Authentication", token);
    }

    @Override
    public void expectedResponse(HttpServletResponse response) {
        responseHolder.set(response);
    }

    @Override
    public void leaveRequestResponse() {
        responseHolder.remove();

    }

    @Override
    public void bypassTokenFor(String r) {
        setBypass.add(r);

    }

    @Override
    public boolean isToBypass(String s) {
        return setBypass.contains(s);
    }
}
