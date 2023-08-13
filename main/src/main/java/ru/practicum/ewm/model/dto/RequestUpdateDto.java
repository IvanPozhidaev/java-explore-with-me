package ru.practicum.ewm.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.RequestUpdateStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestUpdateDto {

    private List<Long> requestIds;
    private RequestUpdateStatus status;
}