package com.secured.api.data;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Access(AccessType.FIELD)
@Table(name="UserDetails")
public class UserAccount  implements UserDetails, Serializable {


    @Id
    private String userId;
    private String password;
    boolean locked;
    Date expertationDate;
    boolean enabled;
    Date passwordExperationDate;
    String  authorities;
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void addRole(String role)
    {
        role = role.toUpperCase();
        StringBuilder builder = new StringBuilder();
        if(getAuthorities().contains(role))
        {
            return;
        }
        if(authorities!=null) {

            builder.append(authorities);
        }
        if(builder.length()>0)
        {
            builder.append("|");

        }
        builder.append(role);
        authorities = builder.toString();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> res = new ArrayList<>() ;
        if(authorities!=null)
        {
            String[] split =authorities.split("\\|");
            for(String s:split)
            {
                if(s.length()>0)
                    res.add(new GrantedAuthority() {
                        @Override
                        public String getAuthority() {
                            return s;
                        }
                    });

            }
        }
        return res;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setExpertationDate(Date expertationDate) {
        this.expertationDate = expertationDate;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setPasswordExperationDate(Date passwordExperationDate) {
        this.passwordExperationDate = passwordExperationDate;
    }




    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getUsername() {
        return userId;
    }


    @Override
    public boolean isAccountNonExpired() {
        if(expertationDate==null)
            return true;
        Date d= new Date(System.currentTimeMillis());
        return expertationDate.after(d);
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
       if(expertationDate==null)
           return true;
        return  new Date(System.currentTimeMillis()).before(passwordExperationDate);
    }

    @Override
    public boolean isEnabled() {
        return !locked;
    }
}
