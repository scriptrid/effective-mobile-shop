package ru.scriptrid.userservice.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.scriptrid.userservice.model.entity.UserEntity;

import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {

    private final String username;
    private final String password;
    private final boolean isAdmin;
    private final boolean isFrozen;
    private final boolean isDeleted;

    public UserDetailsImpl(UserEntity entity) {
        this.username = entity.getUsername();
        this.password = entity.getPassword();
        this.isAdmin = entity.getIsAdmin();
        this.isDeleted = entity.getIsDeleted();
        this.isFrozen = entity.getIsFrozen();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (isAdmin) {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public boolean isFrozen() {
        return isFrozen;
    }
}
