package com.secured.utils;

import com.secured.api.IJwtTokenFactory;
import org.junit.Assert;
import org.junit.Test;

import java.rmi.activation.UnknownObjectException;
import com.secured.api.JwtTokenDef;
import java.util.HashSet;
import java.util.Set;

public class JwtTokenFactoryTest {
    String currToken;
    JwtTokenFactory factory = new JwtTokenFactory();
    Set<String> roles = new HashSet<>();
    {
        roles.add("ROLE_GUEST");
        roles.add("ROLE_USER");
    }

    @Test
    public void batch()
    {
        generateToken();
        restoreToken();
        restoreTokenAnotherPrefix();
        prolongToken();

    }
    String token;
    void generateToken()
    {

        token=factory.generateSecuredToken(new JwtTokenDef("username",roles,"127.0.0.1"));
        Assert.assertNotNull(token);


    }
    void restoreToken()
    {
        Exception e=null;
        JwtTokenDef def=null;

        try {
            def= factory.fromTokenAndValid(token);
        } catch (UnknownObjectException e1) {
            e1.printStackTrace();
            e= e1;
        } catch (IJwtTokenFactory.ExceptionTokenExpired exceptionTokenExpired) {
            exceptionTokenExpired.printStackTrace();
        }
        Assert.assertNull(e);
        Assert.assertEquals(def.getUserId(),"username");
        Assert.assertEquals(def.getRoles(),roles);
    }
    void restoreTokenAnotherPrefix()
    {
        Exception e=null;
        JwtTokenDef def=null;
        factory.setTokenPrefix("Bearer");

        try {
            def= factory.fromTokenAndValid(token);
        } catch (UnknownObjectException e1) {
            e1.printStackTrace();
            e= e1;
        } catch (IJwtTokenFactory.ExceptionTokenExpired exceptionTokenExpired) {
            exceptionTokenExpired.printStackTrace();
        }
        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof UnknownObjectException);
        factory.setTokenPrefix("ourToken");

    }
    void prolongToken() {
        Exception e=null;
        JwtTokenDef def=null;
        try {
            def= factory.fromTokenAndValid(token);
        } catch (UnknownObjectException e1) {
            e1.printStackTrace();
            e= e1;
        } catch (IJwtTokenFactory.ExceptionTokenExpired exceptionTokenExpired) {
            e=exceptionTokenExpired;
            exceptionTokenExpired.printStackTrace();
        }
        String str = factory.prolongToken(def,1000);
        JwtTokenDef pro;

        try {
            pro= factory.fromTokenAndValid(str);
        } catch (UnknownObjectException e1) {
            e1.printStackTrace();
            e= e1;
        } catch (IJwtTokenFactory.ExceptionTokenExpired exceptionTokenExpired) {
            e=exceptionTokenExpired;
            exceptionTokenExpired.printStackTrace();
        }
        Assert.assertNull(e);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        try {
            pro= factory.fromTokenAndValid(str);
        } catch (Exception e1) {
            e1.printStackTrace();
            e= e1;
        }
        Assert.assertNotNull(e);
        Assert.assertTrue("another exception",e instanceof IJwtTokenFactory.ExceptionTokenExpired);

    }
}