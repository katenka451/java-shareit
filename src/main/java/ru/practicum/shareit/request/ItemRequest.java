package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    @NotNull
    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
}