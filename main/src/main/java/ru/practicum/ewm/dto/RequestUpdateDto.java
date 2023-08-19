package ru.practicum.ewm.dto;

import lombok.*;
import ru.practicum.ewm.entity.model.RequestUpdateStatus;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestUpdateDto {
    private List<Long> requestIds;

    private RequestUpdateStatus status;
}