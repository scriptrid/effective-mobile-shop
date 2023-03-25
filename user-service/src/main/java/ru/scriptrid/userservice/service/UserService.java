package ru.scriptrid.userservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.scriptrid.common.dto.TransactionCreateDto;
import ru.scriptrid.common.dto.UserDto;
import ru.scriptrid.userservice.exceptions.InsufficientFundsException;
import ru.scriptrid.userservice.exceptions.UserNotFoundByIdException;
import ru.scriptrid.userservice.exceptions.UserNotFoundByUsernameException;
import ru.scriptrid.userservice.exceptions.UsernameAlreadyExistsException;
import ru.scriptrid.userservice.model.UserDetailsImpl;
import ru.scriptrid.userservice.model.dto.CreateUserDto;
import ru.scriptrid.userservice.model.entity.UserEntity;
import ru.scriptrid.userservice.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;


@Service
@Slf4j
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
            log.warn("The user \"{}\" already exists", dto.username());
            throw new UsernameAlreadyExistsException(dto.username());
        }
        UserEntity userEntity = userRepository.save(toEntity(dto));
        return toUserDto(userEntity);
    }

    @Transactional
    public UserDto setBalance(long id, BigDecimal balance) {
        UserEntity entity = getUserById(id);
        entity.setBalance(balance);

        return toUserDto(entity);
    }

    @Transactional
    public void setFreeze(long id, boolean isFrozen) {
        UserEntity entity = getUserById(id);
        entity.setIsFrozen(isFrozen);

    }

    @Transactional
    public void deleteUser(long id) {
        UserEntity entity = getUserById(id);
        entity.setIsDeleted(true);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("The user \"{}\" not found", username);
                    return new UserNotFoundByUsernameException(username);
                });
        return new UserDetailsImpl(entity);
    }


    private UserEntity toEntity(CreateUserDto dto) {
        UserEntity entity = new UserEntity();
        entity.setUsername(dto.username());
        entity.setPassword(passwordEncoder.encode(dto.password()));
        entity.setEmail(dto.email());

        return entity;
    }

    private UserDto toUserDto(UserEntity entity) {
        return new UserDto(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getIsAdmin()
        );
    }

    public UserDto getUserDtoById(long id) {
        return toUserDto(getUserById(id));
    }

    private UserEntity getUserById(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> {
                    log.warn("The user with id \"{}\" not found", id);
                    return new UserNotFoundByIdException(id);
                }
        );
    }

    public UserDto getUserDtoByUsername(String username) {
        UserEntity entity = userRepository.findByUsername(username).orElseThrow(
                () -> {
                    log.warn("The user \"{}\" not found", username);
                    return new UserNotFoundByUsernameException(username);
                }
        );
        return toUserDto(entity);
    }

    public List<UserDto> getUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(this::toUserDto)
                .toList();
    }

    @Transactional
    public void transfer(TransactionCreateDto dto) {
        UserEntity customer = getUserById(dto.customerId());
        UserEntity seller = getUserById(dto.sellerId());

        if (customer.getBalance().compareTo(dto.total()) < 0) {
            throw new InsufficientFundsException(customer.getBalance(), dto.total());
        }
        customer.setBalance(customer.getBalance().subtract(dto.total()));
        seller.setBalance(seller.getBalance().add(dto.sellersIncome()));
    }
}

