package com.secured.concrete.data;

import com.secured.api.data.ICachedUserDetailAccess;
import com.secured.api.data.IUserDetailService;
import com.secured.api.data.IUserRepository;
import com.secured.api.data.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Author: S.cantor
 * This clas manages user details records and caching.
 * The class is composite object, which uses both  database
 * @see #dbRepository
 * and cache
 * @see #userDetailsNwCache
 *
 */
public class UserDetailServiceImpl implements IUserDetailService {
    @Autowired
    IUserRepository  dbRepository;
    //qualifier is compulsory to be becase of the service may
    @Qualifier(ICachedUserDetailAccess.SYSTEM_QUALIFIER)
    @Autowired
    ICachedUserDetailAccess userDetailsNwCache;
    @Override
    public UserDetails getUserDetailsByUserName(String user) {

        return dbRepository.getOne(user);

    }

    @Override
    public void saveUser(UserAccount ud) {
        dbRepository.saveAndFlush(ud);
    }

    @Override
    public UserDetails getUserDetailsByToken(String token) {
        return  userDetailsNwCache.getUserDetailsByToken(token);
    }

    @Override
    public void updateUseDetails(UserDetails ud, String token,long milliseconds) {
       userDetailsNwCache.updateUseDetails(ud,token,milliseconds);
        dbRepository.save((UserAccount)ud);


    }

    @Override
    public void unlinkUserDetailsFromToken(String token) {
        userDetailsNwCache.unlinkUserDetailsFromToken(token);

    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return getUserDetailsByUserName(s);
    }
}
