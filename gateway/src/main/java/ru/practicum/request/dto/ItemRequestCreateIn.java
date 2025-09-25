package ru.practicum.request.dto;
import jakarta.validation.constraints.NotBlank;

public class ItemRequestCreateIn {
    @NotBlank
    public String description;
}
