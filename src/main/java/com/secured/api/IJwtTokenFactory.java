package com.secured.api;

import java.rmi.activation.UnknownObjectException;
import java.util.concurrent.TimeUnit;

public interface IJwtTokenFactory
{
    void    setTokenPrefix(String pref);
    String  getTokenPrefix();
    void    setTokenDuration(long time, TimeUnit unit);
    String   generateSecuredToken(JwtTokenDef def);
    String   prolongToken(JwtTokenDef def, long millisec);
    JwtTokenDef   fromTokenAndValid(String str) throws UnknownObjectException, ExceptionTokenExpired;
    public class ExceptionTokenExpired extends Exception
    {

    }
}
