package com.secured.api.data;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserDetailService extends UserDetailsService,ICachedUserDetailAccess {

    static final String SYSTEM_QUALIFIER = "fullUserDataService";
    UserDetails getUserDetailsByUserName(String user);
    void saveUser(UserAccount ud);


}
