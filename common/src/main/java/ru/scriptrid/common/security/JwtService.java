package ru.scriptrid.common.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.scriptrid.common.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {


    @Value("${jwt.secretcode}")
    private String secret;

    @Value("${jwt.issuer}")
    private String issuer;


    public String generateUserToken(UserDto dto) throws IllegalArgumentException, MalformedJwtException {

        return Jwts.builder()
                .setSubject(String.valueOf(dto.id()))
                .claim("username", dto.username())
                .claim("email", dto.email())
                .claim("isAdmin", dto.isAdmin())
                .claim("isService", false)
                .setIssuedAt(new Date())
                .setIssuer(issuer)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateServiceToken() throws IllegalArgumentException, MalformedJwtException {
        return Jwts.builder()
                .setSubject(issuer)
                .claim("username", issuer)
                .claim("email", issuer + "@shop.io")
                .claim("isAdmin", true)
                .claim("isService", true)
                .setIssuedAt(new Date())
                .setIssuer(issuer)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
}
