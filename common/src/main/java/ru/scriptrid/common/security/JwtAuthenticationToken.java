package ru.scriptrid.common.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final long id;

    private final String username;

    private final String email;

    private final boolean isAdmin;

    private final boolean isService;

    private final String jwt;

    private final List<GrantedAuthority> authorities = new ArrayList<>();

    public JwtAuthenticationToken(Claims claims, String jwt) {

        super(Collections.emptyList());
        this.id = claims.get("id", Long.class);
        this.username = claims.get("username", String.class);
        this.email = claims.get("email", String.class);
        this.isAdmin = claims.get("isAdmin", Boolean.class);
        this.isService = claims.get("isService", Boolean.class);
        this.jwt = jwt;

        this.authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (isAdmin) {
            this.authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        if (isService) {
            this.authorities.add(new SimpleGrantedAuthority("ROLE_SERVICE"));
        }

    }

    @Override
    public String getName() {
        return username;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalStateException();
    }

    public String getJwt() {
        return jwt;
    }

    public String getEmail() {
        return email;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isService() {
        return isService;
    }

    @Override
    public String toString() {
        return "JwtAuthenticationToken{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", isAdmin=" + isAdmin +
                ", authorities=" + authorities +
                '}';
    }
}
