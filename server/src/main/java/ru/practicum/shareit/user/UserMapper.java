package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {
    public static UserDto toDto(User u) {
        if (u == null) return null;
        return UserDto.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .build();
    }

    public static User fromDto(UserDto dto) {
        if (dto == null) return null;
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }
}