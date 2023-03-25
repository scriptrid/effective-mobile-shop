package ru.scriptrid.common.dto;

public record UserDto(
        long id,
        String username,
        String email,
        boolean isAdmin
) {
}
