package ru.scriptrid.userservice.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.scriptrid.userservice.exceptions.UsernameAlreadyExistsException;
import ru.scriptrid.userservice.model.dto.CreateUserDto;
import ru.scriptrid.userservice.model.dto.UserDto;
import ru.scriptrid.userservice.model.entity.UserDetailsImpl;
import ru.scriptrid.userservice.model.entity.UserEntity;
import ru.scriptrid.userservice.repository.UserRepository;


@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    public UserDto addUser(CreateUserDto dto) throws UsernameAlreadyExistsException {
        if (userRepository.existsByUsername(dto.username())) {
            throw new UsernameAlreadyExistsException();
        }
        UserEntity userEntity = userRepository.save(toEntity(dto));
        return toUserDto(userEntity);
    }

    private UserEntity toEntity(CreateUserDto dto) {
        UserEntity entity = new UserEntity();
        entity.setUsername(dto.username());
        entity.setPassword(passwordEncoder.encode(dto.password()));
        entity.setEmail(dto.email());
        entity.setIsAdmin(false);
        entity.setBalance(0L);

        return entity;
    }

    public UserDto getUser(String username) {
        return toUserDto(userRepository.findByUsername(username).orElseThrow());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found: " + username));
        return new UserDetailsImpl(entity);
    }

    private UserDto toUserDto(UserEntity entity) {
        return new UserDto(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getIsAdmin()
        );
    }
}

