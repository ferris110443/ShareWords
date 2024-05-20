package org.yplin.project.configuration;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class JwtTokenUtil {


    private @Value("${jwt.signKey}") String jwtSignKey;

    private @Value("${jwt.expireTimeAsSec}") long jwtExpireTimeAsSec; // 30 day in sec


    public String generateToken(String email) {
        return Jwts.builder()
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * jwtExpireTimeAsSec))
                .signWith(SignatureAlgorithm.HS256, jwtSignKey)
                .compact();
    }


    public Date getExpirationDateFromToken(String token) {
        return extractClaims(token).getExpiration();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSignKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUserEmail(String token) {
        Claims claims = extractClaims(token);
        return (claims != null) ? claims.get("email", String.class) : null;
    }
    
}