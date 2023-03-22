package ru.scriptrid.userservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.scriptrid.userservice.model.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtility {


    @Value("${jwt-secretcode}")  //Подтягивает из application.yml jwt-secretcode для генерации токена
    private String secret;

    @Value("${jwt-issuer}")
    private String issuer;


    //Метод для подписи и создания JWT-токена на базе секретной комбинациии
    public String generateToken(UserDto dto) throws IllegalArgumentException, MalformedJwtException {

        return Jwts.builder()
                .setSubject(String.valueOf(dto.id()))
                .claim("username", dto.username())
                .claim("email", dto.email())
                .claim("isAdmin", dto.isAdmin())
                .setIssuedAt(new Date())
                .setIssuer(issuer)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
}
