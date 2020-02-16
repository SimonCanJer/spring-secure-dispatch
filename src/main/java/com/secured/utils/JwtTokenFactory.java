package com.secured.utils;

import com.secured.api.IJwtTokenFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.rmi.activation.UnknownObjectException;

import com.secured.api.JwtTokenDTO;
import com.secured.api.JwtTokenDef;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JwtTokenFactory implements IJwtTokenFactory {

    @Value("${security.jwt.issuer}")
    String issuer;
    String prefix = "ourToken";
    @Value("${secured.authentication.timeout_sec}")
    Long dur = 600L;
    TimeUnit units = TimeUnit.SECONDS;
    @Value("${security.jwt.secret}")
    String encriptionKey ;
    String encriptionKey2="wsxcdeijm1qaz";
    String issuer2="localSecurity";

    @PostConstruct
    void init()
    {
        if(dur==null)
            dur=600L;
    }
    @Override
    public void setTokenPrefix(String prefix) {
        this.prefix = prefix;

    }

    @Override
    public String getTokenPrefix() {
        return prefix;
    }

    @Override
    public void setTokenDuration(long time, TimeUnit unit) {
    }


    @Override
    public String generateSecuredToken(JwtTokenDef def) {
        Claims claims = getClaims(def);
        LocalDateTime exp = LocalDateTime.now();
        switch (units) {
            case MINUTES:
                exp = exp.plusMinutes(dur);
                break;
            case SECONDS:
                exp = exp.plusSeconds(dur);
                break;
            case HOURS:
                exp = exp.plusHours(dur);
            default:
                exp = exp.plusMinutes(dur);
        }
        return Jwts.builder().addClaims(claims).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(Date.from(exp.atZone(ZoneId.systemDefault()).toInstant())).setId(def.getUserId()).setIssuer("abcs").signWith(SignatureAlgorithm.HS512, encriptionKey).compact();
    }

    private Claims getClaims(JwtTokenDef def) {
        Claims claims = Jwts.claims();
        JwtTokenDTO dto = new JwtTokenDTO(prefix, def);

        claims.put("token", dto);
       return claims;
    }

    @Override
    public String prolongToken(JwtTokenDef def, long millisec) {

        validateRequisites();
        Claims claims = getClaims(def);
        long now = System.currentTimeMillis();
        return Jwts.builder().addClaims(claims).setIssuedAt(new Date(now)).setExpiration(new Date(now + millisec)).setId(def.getUserId()).setIssuer(issuer).signWith(SignatureAlgorithm.HS512, encriptionKey).compact();
    }

    private void validateRequisites() {
        if(issuer==null)
            issuer=issuer2;
        if(encriptionKey==null)
            encriptionKey= encriptionKey2;
    }


    @Override
    public JwtTokenDef fromTokenAndValid(String str) throws UnknownObjectException,ExceptionTokenExpired {
        validateRequisites();
        try {
            Claims claims = Jwts.parser()
                .setSigningKey(encriptionKey)
                .parseClaimsJws(str)
                .getBody();
            Map<String, Object> d= (Map<String, Object>) claims.get("token");
            JwtTokenDTO dto = new JwtTokenDTO(d);
            assert (dto.getPrefix().equals(prefix));
            JwtTokenDef def = dto.getPayload();
            Date dt = claims.getExpiration();
            Date now = new Date(System.currentTimeMillis());
            if (dt.before(now))
                throw new ExceptionTokenExpired();
            return def;
        } catch (Throwable e) {
            if(e instanceof ExpiredJwtException)
                throw new ExceptionTokenExpired();
            throw new UnknownObjectException("not my token");
        }
    }
}
