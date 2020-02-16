package com.secured.api;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JwtTokenDef {
    public JwtTokenDef(Map<String,Object> payload) {
        userId= (String) payload.get("userId");
        additional = (String) payload.get("additional");
        roles= new HashSet<>();
        roles.addAll((Collection<String>) payload.get("roles"));
    }

    public String getUserId() {
        return userId;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public String getAdditional() {
        return additional;
    }

    private  String userId;
    private  Set<String>  roles;
    private  String  additional;

    public JwtTokenDef(String userId, Set<String> roles, String additional) {
        this.userId = userId;
        this.roles = roles;
        this.additional = additional;

    }
}
