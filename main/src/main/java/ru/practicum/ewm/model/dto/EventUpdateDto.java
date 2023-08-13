package ru.practicum.ewm.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateDto {

    @Size(min = 3, max = 120)
    private String title;
    @Size(min = 20, max = 7000)
    private String description;
    @Size(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private LocationDto location;
    private Integer participantLimit;
    private Boolean requestModeration;
    private Boolean paid;
    private EventStateAction stateAction;
}