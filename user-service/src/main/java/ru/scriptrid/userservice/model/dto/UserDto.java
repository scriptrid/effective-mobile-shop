package ru.scriptrid.userservice.model.dto;

public record UserDto(
        int id,
        String username,
        String email,
        boolean isAdmin
) {
}
