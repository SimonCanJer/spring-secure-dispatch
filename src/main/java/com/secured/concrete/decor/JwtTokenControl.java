package com.secured.concrete.decor;

import com.secured.api.IJwtTokenFactory;
import com.secured.api.JwtTokenDef;
import com.secured.api.data.IUserDetailService;
import com.secured.api.decor.ICredentialsToToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The class implements filter which manages authentication by token and impersonation
 * @Author Simon Cantor
 */
public class JwtTokenControl extends OncePerRequestFilter {
    @Autowired
    IImpersonationSinkAccess impersonationSink;
    @Autowired
    @Qualifier(IUserDetailService.SYSTEM_QUALIFIER)
    IUserDetailService userDetails;
    @Autowired
    IJwtTokenFactory tokenManager;
    @Autowired
    ICredentialsToToken tokenRegistration;

    @Autowired
    IJustInRequestAdvisor justInRequest;
    @Value("${secured.authentication.timeout_sec}")
    Long dur = 600L;

    @PostConstruct
    void init()
    {
        if(dur==null)
            dur=600L;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {


        String securityHeader = httpServletRequest.getHeader("Authentication");
        //String token = null;
        String path = httpServletRequest.getRequestURI().substring(httpServletRequest.getContextPath().length());
        if(justInRequest.isToBypass(path))
        {
            justInRequest.expectedResponse(httpServletResponse);
            filterChain.doFilter(httpServletRequest,httpServletResponse);

            return ;
        }
        if (securityHeader != null) {

            try {
                JwtTokenDef def = tokenManager.fromTokenAndValid(securityHeader);
                String s = def.getAdditional();
               /*
                  Suspeceous source of request.
                */
                if (!httpServletRequest.getRemoteAddr().equals(s)) {
                    removeAllSecurityRemains(httpServletResponse, securityHeader, HttpServletResponse.SC_CONFLICT);
                    return;
                }
                String userName = def.getUserId();
                Set<String> roles = def.getRoles();
                UserDetails ud = userDetails.getUserDetailsByToken(securityHeader);
                if (!ud.isAccountNonLocked() || !ud.isEnabled()) {
                    removeAllSecurityRemains(httpServletResponse, securityHeader, HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                UsernamePasswordAuthenticationToken ticket = new UsernamePasswordAuthenticationToken(userName, ud.getPassword(), ud.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(ticket);
            } catch (Exception e) {
                removeAllSecurityRemains(httpServletResponse, securityHeader, HttpServletResponse.SC_UNAUTHORIZED);
                return;

            }
        } else {

            UserDetails ud = justInRequest.adviceTokenNotFoundCase(httpServletRequest);
            /// this is needed for authentication in controller in order to add headers
            justInRequest.expectedResponse(httpServletResponse);
            if (ud != null) {
                UsernamePasswordAuthenticationToken ticket= new     UsernamePasswordAuthenticationToken(ud.getUsername(),ud.getPassword(),ud.getAuthorities());
                Set<String> set = ticket.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toSet());
                securityHeader = tokenRegistration.trailUserCredentials(ticket.getName(), null, set);
                userDetails.updateUseDetails(ud,securityHeader,1000*dur);
                SecurityContextHolder.getContext().setAuthentication(ticket);
                httpServletResponse.setHeader("Authentication", securityHeader);
            }

        }
        if(securityHeader!=null)
        {
            impersonationSink.justAuthenticated(securityHeader);
        }
       filterChain.doFilter(httpServletRequest, httpServletResponse);
        if(securityHeader!=null)
        {
            impersonationSink.leavingAuthenticatedRequest(securityHeader);
        }

        tokenRegistration.pollToken();
        justInRequest.leaveRequestResponse();

    }

    private void removeAllSecurityRemains(HttpServletResponse httpServletResponse, String securityHeader, int status) {
        userDetails.unlinkUserDetailsFromToken(securityHeader);
        httpServletResponse.getHeaders("Authentication").clear();
        httpServletResponse.setStatus(status);
        SecurityContextHolder.clearContext();
    }
}
