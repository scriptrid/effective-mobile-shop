package ru.scriptrid.userservice.controller;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.scriptrid.userservice.model.dto.CreateUserDto;
import ru.scriptrid.userservice.model.dto.LoginUserDto;
import ru.scriptrid.common.dto.UserDto;
import ru.scriptrid.common.security.JwtService;
import ru.scriptrid.userservice.service.UserService;


@Slf4j
@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }


    @PostMapping("/register")
    public ResponseEntity<UserDto> newUser(@RequestBody @Valid CreateUserDto dto) {
        UserDto user = userService.addUser(dto);
        String token = jwtService.generateUserToken(user);
        return ResponseEntity.ok().header("Authorization", token).body(user);
    }


    @PostMapping("/login")
    public ResponseEntity<UserDto> login(HttpServletRequest req, @RequestBody LoginUserDto dto) {
        if (!doLogin(req, dto.username(), dto.password())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        UserDto user = userService.getUserDtoByUsername(dto.username());
        String token = jwtService.generateUserToken(user);
        return ResponseEntity.ok().header("Authorization", token).body(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest req) {
        try {
            req.logout();
            return ResponseEntity.ok().build();
        } catch (ServletException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

    }

    private boolean doLogin(HttpServletRequest req, String username, String password) {
        try {
            req.login(username, password);
        } catch (ServletException e) {
            log.error("Error while login", e);
            return false;
        }
        return true;

    }
}
