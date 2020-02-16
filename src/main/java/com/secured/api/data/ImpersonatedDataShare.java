package com.secured.api.data;

import java.io.Serializable;

/**
 * @Author: Simon Cantor
 * The interface deaclare functionality for caching data of an impersonated user
 */
public interface ImpersonatedDataShare {

    /**
     * puts data to cache
     * @param token user token
     * @param data use data
     * @param millisecons time to keep data
     * @param <T> a seriallizable type of data
     */
    <T extends Serializable> void  cacheData(String token, T data,long millisecons);

    /**
     * returns impersonated data
     * @param token
     * @param <T>
     * @return
     */
    <T extends Serializable> T getData(String token);

    /**
     * remove the impersonated data from the cache
     * @param token
     */
    void forget(String token);

}
