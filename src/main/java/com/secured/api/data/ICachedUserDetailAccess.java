package com.secured.api.data;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * @Author: Simon Cantor
 * This interface declares access to a cache of user data
 */
public interface ICachedUserDetailAccess  {

    final String SYSTEM_QUALIFIER = "data.cache.userDetails";

    /**
     * retrieve user details by token
     * @param token - value of token of user to retrieve details of
     * @return user details (or null)
     */
    UserDetails getUserDetailsByToken(String token);

    /**
     * inserts/updates user details in cache using the token of the user as
     * a key for future retrieve
     * @param ud the user details to keep
     * @param token the token
     * @param milliseconds how much milliseconds the entry will be in a cache
     */
    void        updateUseDetails(UserDetails ud,String token, long milliseconds);

    /**
     * removes information about user details
     * @param token token
     */
    void unlinkUserDetailsFromToken(String token);

}
