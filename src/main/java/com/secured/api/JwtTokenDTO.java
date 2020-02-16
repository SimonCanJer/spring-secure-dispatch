package com.secured.api;

import java.util.Map;

public class JwtTokenDTO {
     String prefix;
     JwtTokenDef  payload;

    public JwtTokenDTO(Map<String,Object> d) {
        fromMap(d);
    }

    public void fromMap(Map<String, Object> map)
    {
        prefix= (String) map.get("prefix");
        payload = new JwtTokenDef((Map<String,Object>) map.get("payload"));


    }

    public String getPrefix() {
        return prefix;
    }

    public JwtTokenDef getPayload() {
        return payload;
    }

    public JwtTokenDTO(String prefix, JwtTokenDef payload) {
        this.prefix = prefix;
        this.payload = payload;
    }
}
