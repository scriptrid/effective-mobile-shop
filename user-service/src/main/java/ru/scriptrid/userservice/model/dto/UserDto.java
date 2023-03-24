package ru.scriptrid.userservice.model.dto;

public record UserDto(
        long id,
        String username,
        String email,
        boolean isAdmin
) {
}
