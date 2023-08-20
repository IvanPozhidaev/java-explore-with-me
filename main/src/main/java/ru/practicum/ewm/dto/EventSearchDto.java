package ru.practicum.ewm.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.entity.model.EventSort;
import ru.practicum.ewm.entity.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSearchDto {
    private String text;

    private List<EventState> states;

    private Long[] categories;

    private Boolean paid;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;

    @Builder.Default
    private Boolean onlyAvailable = false;

    @Builder.Default
    private Integer from = 0;

    @Builder.Default
    private Integer size = 10;

    private EventSort sort;

    private Long[] users;
}