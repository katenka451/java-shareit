package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequestDto {
    @NotNull
    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
}