package com.secured.api.data;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.AssertTrue;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import static org.junit.Assert.*;

public class UserAccountTest {

    UserAccount account = new UserAccount();
    @Test
    public void Test() {
        Throwable found = null;
        try {
            account.addRole("GUEST");
            account.addRole("ADMIN");
        }
        catch(Throwable e)
        {
            found = e;
        }
        Assert.assertNull("exception when adding",found);
        getAuthorities();

    }
    public void getAuthorities() {
        Collection<? extends GrantedAuthority> aut=null;
        aut=account.getAuthorities();
        Iterator<? extends GrantedAuthority> it=aut.iterator();
        Assert.assertEquals("wrong size",aut.size(),2);

        Assert.assertEquals("!GUEST inside",it.next().getAuthority(),"GUEST");
        Assert.assertEquals("!ADMIN inside",it.next().getAuthority(),"ADMIN");
        testLocked();
        testEnabled();
        try {
            testExpertationDate();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            testPasswordExperationDate();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




    public void testLocked() {
        account.setLocked(true);
        assertFalse(account.isAccountNonLocked());
    }


    public void testExpertationDate() throws InterruptedException {
        account.setExpertationDate(null);
        Exception found =null ;
        try
        {
            assertTrue("null experation- bad result ",account.isAccountNonExpired());
        }
        catch(Exception e)
        {
            found =e;
        }
        Assert.assertNull("null experation - execption",found);

        account.setExpertationDate(new Date(System.currentTimeMillis()));
        Thread.sleep(100);
        Assert.assertFalse("immediate account expertaion wrong result",account.isAccountNonExpired());
        account.setExpertationDate(new Date(System.currentTimeMillis()+1000000));
        Thread.sleep(100);
        Assert.assertTrue(" expired now, when shoud be expired after 1000000",account.isAccountNonExpired());
    }


    public void testEnabled() {
        account.setEnabled(false);
        isEnabled();

    }
    public void isEnabled() {
        Assert.assertFalse(account.isEnabled());
    }

    public void testPasswordExperationDate() throws InterruptedException {
        account.setPasswordExperationDate(null);
        Exception found =null ;
        try
        {
            assertTrue("password:null experation- bad result ",account.isAccountNonExpired());
        }
        catch(Exception e)
        {
            found =e;
        }
        Assert.assertNull("password: null experation - exception",found);

        account.setPasswordExperationDate(new Date(System.currentTimeMillis()));
        Thread.sleep(100);
        Assert.assertFalse("immediate password expertaion wrong result",account.isCredentialsNonExpired());
        account.setPasswordExperationDate(new Date(System.currentTimeMillis()+1000000));
        Thread.sleep(100);
        Assert.assertTrue(" password expired now, when shoud be expired after 1000000",account.isCredentialsNonExpired());
    }




}