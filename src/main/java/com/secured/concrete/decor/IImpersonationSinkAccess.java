package com.secured.concrete.decor;

/**
 * The interface exposes methods to be called when"
 * a user is authenticated,
 * a request is done
 * @Author Simon Cantor
 */
public interface IImpersonationSinkAccess {

    void justAuthenticated(String token);

    void leavingAuthenticatedRequest(String token);
}
