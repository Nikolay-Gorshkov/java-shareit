package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserIn {
    public Long id;

    @NotBlank
    public String name;

    @NotBlank
    @Email
    public String email;
}