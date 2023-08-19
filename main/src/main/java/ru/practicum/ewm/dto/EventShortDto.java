package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    private Long id;

    private String title;

    private String description;

    private String annotation;

    private CategoryDto category;

    private UserDto initiator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Long confirmedRequests;

    private Boolean paid;

    private Long views;
}