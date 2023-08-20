package ru.practicum.ewm.util;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.Request;
import ru.practicum.ewm.entity.model.RequestStatus;

import java.util.List;

@UtilityClass
public class EventUtils {
    public long countConfirmedRequests(Event event) {
        List<Request> allRequests = event.getAllRequests();

        long result = 0L;
        if (allRequests != null) {
            for (Request rm : allRequests) {
                if (rm.getStatus().equals(RequestStatus.CONFIRMED)) {
                    result++;
                }
            }
        }
        return result;
    }
}