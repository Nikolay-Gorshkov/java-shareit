package ru.practicum.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ItemDtoIn {
    public Long id;

    @NotBlank
    public String name;

    @NotBlank
    public String description;

    @NotNull
    public Boolean available;
    public Long requestId;
}