package com.secured.concrete.decor;

import com.secured.api.data.ICachedUserDataAccess;
import com.secured.api.data.IUserDetailService;
import com.secured.api.data.ImpersonationListener;
import com.secured.api.decor.SecurityConfigAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * The configuration class initializes configures and populates beans
 * managing securtity decoration is based in Java Web Token
 * @Author Simon Cantor
 */
@EnableWebSecurity
@Configuration
public class ConfigSecurityDecor {

    @Value("${secured.authentication.timeout_sec}")
    Long dur = 600L;
    @Autowired
    IJustInRequestAdvisor advisor;
    @Autowired
    ICachedUserDataAccess cachedDataAccess;
    @Autowired
    @Qualifier(IUserDetailService.SYSTEM_QUALIFIER)
    IUserDetailService uds;

    /**
     * Creates and return the bean-implementation of
     * the
     * @see SecurityConfigAPI
     * interface
     *
     * @return the announced interface
     */
    @Bean
    SecurityConfigAPI securityConfigAPI()
    {
        return new SecurityConfigAPI() {
            /**
             * called in security configurer to intall user detail service
             * @param builder
             * @throws Exception
             */
            @Override
            public void installCustomUserDetailService(AuthenticationManagerBuilder builder) throws Exception {
                builder.userDetailsService(uds);
            }

            /**
             * The implementation installs token management filter
             * in the filter chain before
             * @see BasicAuthenticationFilter
             * @param sec - Spring provided object
             * @throws Exception
             */
            @Override
            public void installTokenControlFilter(HttpSecurity sec) throws Exception {
                sec.exceptionHandling().authenticationEntryPoint(entryPoint());
                sec.addFilterBefore(jwtTokenFilter(),BasicAuthenticationFilter.class);
                sec.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

            }

            /**
             * The method called from security configurer to setup access to resources
             * @param http  Spring provided object
             * @param protectedResources
             * @param openResources
             * @param def
             * @throws Exception
             */
            @Override
            public void configResourseProtection(HttpSecurity http, Map<String, Set<String>> protectedResources, Set<String> openResources, UNamePasswordDef def) throws Exception {
                http.csrf().disable().httpBasic().disable();
                advisor.adviceSettings(def);
                ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl perm[] = new ExpressionUrlAuthorizationConfigurer.AuthorizedUrl[]{null};
               // permit free access to resources are delared as open
                if(openResources!=null && openResources.size()>0) {

                    ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizer = http.authorizeRequests();
                    openResources.forEach(r -> {
                      perm[0]=authorizer.antMatchers(r);
                        advisor.bypassTokenFor(r);
                    });
                    perm[0].permitAll();
                }
                perm[0]=null;
                /*
                 * Limit access to resources are protected by beloning to a role
                 */
                protectedResources.entrySet().stream().forEach(s->
                {
                    try {
                        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizer = http.authorizeRequests();
                        perm[0]=authorizer.antMatchers(s.getKey());
                        for(String r:s.getValue()) {
                            perm[0].hasAnyRole(r);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(perm[0]!=null)
                        perm[0].authenticated();

                });
                installTokenControlFilter(http);
            }

        };
    }
    @Bean
    OncePerRequestFilter jwtTokenFilter()
    {
        return new JwtTokenControl();
    }
    @Bean
    AuthenticationEntryPoint entryPoint()
    {
        return new HandleAuthenticationFailed();
    }
    @Bean
    com.secured.api.decor.ICredentialsToToken  tokenToCredentialLink()
    {
        return new CredentialsToToken();
    }

    private Consumer<Serializable> onImpersonated;
    private Consumer<Consumer<Serializable>> onLeaving;

    /**
     * Export subscriber (one) to be notified about impersonation success
     * @return the related interface
     * @see ImpersonationListener
     */
    @Bean
   ImpersonationListener  impersonationListener()
   {
       return new ImpersonationListener() {
           @Override
           public void onImpersonated(Consumer<Serializable> listener) {
               onImpersonated= listener;
           }

           @Override
           public void onLeavingImpersonatedRequest(Consumer<Consumer<Serializable>> listener) {
               onLeaving=listener;
          }
       };
   }

   @Bean
    IImpersonationSinkAccess impersonationSinkAccess()
   {

       return new IImpersonationSinkAccess() {

           @Override
           public void justAuthenticated(String token) {
                if(onImpersonated!=null)
                {

                    onImpersonated.accept(cachedDataAccess.getImpersonationDataSharing().getData(token));
                }

           }

           @Override
           public void leavingAuthenticatedRequest(String token) {
               if(onLeaving!=null)
               {
                   onLeaving.accept((ser)->{cachedDataAccess.getImpersonationDataSharing().cacheData(token,ser,dur);});
               }

           }
       };
   }

    /**
     * populates bean- implementation of
     * @see IJustInRequestAdvisor
     * @return the instance of the class
     */
   @Bean
    IJustInRequestAdvisor justInRequestAuthAdvisor()
   {
       return new JustInRequestAuthAdvisor();
   }



}
