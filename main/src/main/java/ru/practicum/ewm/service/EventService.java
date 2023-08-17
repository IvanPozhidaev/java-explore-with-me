package ru.practicum.ewm.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.ewm.converter.CategoryConverter;
import ru.practicum.ewm.converter.EventConverter;
import ru.practicum.ewm.converter.RequestConverter;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.Location;
import ru.practicum.ewm.entity.QEvent;
import ru.practicum.ewm.entity.Request;
import ru.practicum.ewm.entity.model.*;
import ru.practicum.ewm.exception.MainNotFoundException;
import ru.practicum.ewm.exception.MainParamConflictException;
import ru.practicum.ewm.exception.MainParameterException;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.LocationRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.collective.StatsDto;
import ru.practicum.ewm.util.EventUtils;
import ru.practicum.ewm.util.PageHelper;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;
    private final CategoryService categoryService;

    @Autowired
    public EventService(EventRepository eventRepository,
                        UserRepository userRepository,
                        RequestRepository requestRepository,
                        LocationRepository locationRepository,
                        StatsClient statsClient,
                        CategoryService categoryService) {
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.statsClient = statsClient;
        this.categoryService = categoryService;
    }

    public EventFullDto addEvent(Long userId, EventDto eventDto) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new MainNotFoundException("User with id=" + userId + " was not found"));

        var dateTimeNow = LocalDateTime.now();
        Duration duration = Duration.between(dateTimeNow, eventDto.getEventDate());
        if (duration.toSeconds() <= 7200) {
            throw new MainParamConflictException("Event date must be not earlier than two hours later");
        }

        var createdEvent = EventConverter.convertToModel(user, eventDto);
        var category = CategoryConverter.convertToModel(categoryService.getCategoryById(eventDto.getCategory()));
        createdEvent.setCategory(category);
        createdEvent.setCreatedOn(LocalDateTime.now());
        createdEvent.setState(EventState.PENDING);
        createdEvent.setConfirmedRequests(0);

        var check = locationRepository.findByLatAndLon(eventDto.getLocation().getLat(), eventDto.getLocation().getLon());
        if (check.size() == 0) {
            Location lc = new Location();
            lc.setLat(eventDto.getLocation().getLat());
            lc.setLon(eventDto.getLocation().getLon());
            var after = locationRepository.save(lc);
            createdEvent.setLocation(after);
        } else {
            createdEvent.setLocation(check.get(0));
        }

        var afterCreate = eventRepository.save(createdEvent);
        return EventConverter.convertToDtoFull(afterCreate);
    }

    public List<EventShortDto> getAllEventsByInitiatorPrivate(Long userId, int from, int size) {
        PageRequest pageRequest = PageHelper.createRequest(from, size);
        var result = eventRepository.findAllByInitiatorId(userId, pageRequest).getContent();
        if (result.size() == 0) {
            return new ArrayList<>();
        }
        return EventConverter.mapToShortDto(result);
    }

    public EventFullDto getEventByIdPrivate(Long userId, Long eventId) {
        var foundEvent = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new MainNotFoundException(
                        "Event with id=" + eventId + " and added by user id=" + userId + " was not found"));
        var result = EventConverter.convertToDtoFull(foundEvent);
        result.setViews(getViews(foundEvent));
        return result;
    }

    public EventFullDto updateEventPrivate(Long userId, Long eventId, EventUpdateDto eventDto) {
        var eventToUpd = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new MainNotFoundException(
                        "Event with id=" + eventId + " and added by user id=" + userId + " was not found"));

        if (eventToUpd.getState().equals(EventState.PUBLISHED)) {
            throw new MainParamConflictException("Updated event must be not published");
        }
        var dateTimeNow = LocalDateTime.now();
        if (eventDto.getEventDate() != null) {
            if (eventDto.getEventDate().isBefore(dateTimeNow) || Duration.between(dateTimeNow, eventDto.getEventDate()).toSeconds() <= 7200) {
                throw new MainParamConflictException("Event date must be not earlier than two hours later");
            }
            eventToUpd.setEventDate(eventDto.getEventDate());
        }
        if (eventDto.getAnnotation() != null) {
            eventToUpd.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getCategory() != null) {
            var category = CategoryConverter.convertToModel(categoryService.getCategoryById(eventDto.getCategory()));
            eventToUpd.setCategory(category);
        }
        if (eventDto.getDescription() != null) {
            eventToUpd.setDescription(eventDto.getDescription());
        }
        if (eventDto.getLocation() != null) {
            var loc = locationRepository.findByLatAndLon(eventDto.getLocation().getLat(), eventDto.getLocation().getLon());
            if (loc.size() == 0) {
                Location lc = new Location();
                lc.setLat(eventDto.getLocation().getLat());
                lc.setLon(eventDto.getLocation().getLon());
                var after = locationRepository.save(lc);
                eventToUpd.setLocation(after);
            } else {
                eventToUpd.setLocation(loc.get(0));
            }
        }
        if (eventDto.getPaid() != null) {
            eventToUpd.setPaid(eventDto.getPaid());
        }
        if (eventDto.getParticipantLimit() != null) {
            eventToUpd.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getRequestModeration() != null) {
            eventToUpd.setRequestModeration(eventDto.getRequestModeration());
        }
        if (eventDto.getTitle() != null) {
            eventToUpd.setTitle(eventDto.getTitle());
        }
        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(EventStateAction.SEND_TO_REVIEW)) {
                if (!eventToUpd.getState().equals(EventState.CANCELED)) {
                    throw new MainParamConflictException("Cannot send to review if state is not canceled");
                }
                eventToUpd.setState(EventState.PENDING);
            } else if (eventDto.getStateAction().equals(EventStateAction.CANCEL_REVIEW)) {
                if (!eventToUpd.getState().equals(EventState.PENDING)) {
                    throw new MainParamConflictException("Cannot cancel event if it is not state pending");
                }
                eventToUpd.setState(EventState.CANCELED);
            } else {
                throw new MainParamConflictException("Incorrect state action");
            }
        }
        var after = eventRepository.save(eventToUpd);
        return EventConverter.convertToDtoFull(after);
    }

    public List<RequestDto> getEventRequests(Long userId, Long eventId) {
        var check = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (check == null) {
            throw new MainNotFoundException("Event with id=" + eventId + " and added by user id=" + userId + " was not found");
        }
        var listRequests = requestRepository.findByEventId(eventId);
        return RequestConverter.mapToDto(listRequests);
    }

    public RequestUpdateResultDto updateStatusRequestsForEvent(Long userId, Long eventId, RequestUpdateDto requestDto) {
        var event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new MainNotFoundException(
                        "Event with id=" + eventId + " and added by user id=" + userId + " was not found"));

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new MainParameterException("The limit of participation in the event has been reached");
        }

        RequestUpdateResultDto afterUpdateStatus = new RequestUpdateResultDto();
        var allRequests = event.getAllRequests().stream()
                .collect(Collectors.toMap(Request::getId, i -> i));
        var selectedRequests = requestDto.getRequestIds()
                .stream()
                .map(allRequests::get)
                .collect(Collectors.toList());
        if (selectedRequests.stream().anyMatch(Objects::isNull)) {
            throw new MainParameterException("Request not found for this event");
        }
        boolean check = selectedRequests.stream()
                .anyMatch(r -> !Objects.equals(r.getStatus(), RequestStatus.PENDING));
        if (check) {
            throw new MainParamConflictException("Request must have status PENDING");
        }
        if (event.getRequestModeration().equals(true) || event.getParticipantLimit() != 0) {
            long confReq = EventUtils.countConfirmedRequests(event);
            for (Request r : selectedRequests) {
                if (event.getParticipantLimit() > confReq) {
                    if (requestDto.getStatus().equals(RequestUpdateStatus.CONFIRMED)) {
                        r.setStatus(RequestStatus.CONFIRMED);
                        confReq++;
                        if (event.getParticipantLimit() == confReq) {
                            for (Request rm : event.getAllRequests()) {
                                if (rm.getStatus().equals(RequestStatus.PENDING)) {
                                    rm.setStatus(RequestStatus.REJECTED);
                                    requestRepository.save(rm);
                                }
                            }
                        }
                        var rSaved = requestRepository.save(r);
                        afterUpdateStatus.getConfirmedRequests().add(RequestConverter.convertToDto(rSaved));
                    }
                    if (requestDto.getStatus().equals(RequestUpdateStatus.REJECTED)) {
                        r.setStatus(RequestStatus.REJECTED);
                        var rSaved = requestRepository.save(r);
                        afterUpdateStatus.getRejectedRequests().add(RequestConverter.convertToDto(rSaved));
                    }
                } else {
                    if (requestDto.getStatus().equals(RequestUpdateStatus.CONFIRMED)) {
                        throw new MainParamConflictException("The participant limit has been reached");
                    }
                }
            }
        }
        return afterUpdateStatus;
    }

    public List<EventFullDto> searchEventsAdmin(
            Long[] users,
            EventState states,
            Long[] categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size
    ) {
        PageRequest pageRequest = PageHelper.createRequest(from, size);
        QEvent qModelAdmin = QEvent.event;
        BooleanBuilder predicateAdmin = new BooleanBuilder();
        if (users != null) {
            predicateAdmin.and(qModelAdmin.initiator.id.in(users));
        }
        if (states != null) {
            predicateAdmin.and(qModelAdmin.state.in(states));
        }
        if (categories != null) {
            predicateAdmin.and(qModelAdmin.category.id.in(categories));
        }
        if (rangeStart != null) {
            predicateAdmin.and(qModelAdmin.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            predicateAdmin.and(qModelAdmin.eventDate.before(rangeEnd));
        }
        List<Event> foundEventsAdmin = new ArrayList<>();
        eventRepository.findAll(predicateAdmin).forEach(foundEventsAdmin::add);
        if (foundEventsAdmin.size() == 0) {
            return new ArrayList<>();
        }
        List<Event> pageList = foundEventsAdmin.stream()
                .skip(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .collect(Collectors.toList());
        var result = EventConverter.mapToDtoFull(pageList);
        setViewsForListShortDto(result);
        return result;
    }

    public EventFullDto updateEventByAdmin(Long eventId, EventUpdateDto eventDto) {
        var check = eventRepository.findById(eventId);
        if (check.isEmpty()) {
            throw new MainNotFoundException("Event with id=" + eventId + " was not found");
        }
        var eventToUpdAdmin = check.get();
        var dateTimeNow = LocalDateTime.now();
        if (eventDto.getEventDate() != null) {
            if (eventDto.getEventDate().isBefore(dateTimeNow) || Duration.between(dateTimeNow, eventDto.getEventDate()).toSeconds() <= 7200) {
                throw new MainParamConflictException("Event date must be not earlier than two hours later");
            }
            eventDto.setEventDate(eventDto.getEventDate());
        }
        if (eventDto.getAnnotation() != null) {
            eventToUpdAdmin.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getCategory() != null) {
            var category = CategoryConverter.convertToModel(categoryService.getCategoryById(eventDto.getCategory()));
            eventToUpdAdmin.setCategory(category);
        }
        if (eventDto.getDescription() != null) {
            eventToUpdAdmin.setDescription(eventDto.getDescription());
        }
        if (eventDto.getLocation() != null) {
            var loc = locationRepository.findByLatAndLon(eventDto.getLocation().getLat(), eventDto.getLocation().getLon());
            if (loc.size() == 0) {
                Location lc = new Location();
                lc.setLat(eventDto.getLocation().getLat());
                lc.setLon(eventDto.getLocation().getLon());
                var after = locationRepository.save(lc);
                eventToUpdAdmin.setLocation(after);
            } else {
                eventToUpdAdmin.setLocation(loc.get(0));
            }
        }
        if (eventDto.getPaid() != null) {
            eventToUpdAdmin.setPaid(eventDto.getPaid());
        }
        if (eventDto.getParticipantLimit() != null) {
            eventToUpdAdmin.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getRequestModeration() != null) {
            eventToUpdAdmin.setRequestModeration(eventDto.getRequestModeration());
        }
        if (eventDto.getTitle() != null) {
            eventToUpdAdmin.setTitle(eventDto.getTitle());
        }
        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(EventStateAction.PUBLISH_EVENT)) {
                if (!eventToUpdAdmin.getState().equals(EventState.PENDING)) {
                    throw new MainParamConflictException("Cannot publish event because it's not in the pending state");
                }
                var datePublish = LocalDateTime.now();
                if (eventToUpdAdmin.getEventDate().isBefore(datePublish) || Duration.between(datePublish, eventToUpdAdmin.getEventDate()).toSeconds() <= 3600) {
                    throw new MainParamConflictException("Event date must be not earlier than one hour before published");
                }
                eventToUpdAdmin.setState(EventState.PUBLISHED);
                eventToUpdAdmin.setPublishedOn(datePublish);
            } else if (eventDto.getStateAction().equals(EventStateAction.REJECT_EVENT)) {
                if (!eventToUpdAdmin.getState().equals(EventState.PENDING)) {
                    throw new MainParamConflictException("Cannot reject event because it's in the published state");
                }
                eventToUpdAdmin.setState(EventState.CANCELED);
            } else {
                throw new MainParamConflictException("Incorrect state action");
            }
        }
        var after = eventRepository.save(eventToUpdAdmin);
        return EventConverter.convertToDtoFull(after);
    }

    public List<EventShortDto> getEventsPublic(
            String text,
            Long[] categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            EventSort sort,
            int from,
            int size,
            HttpServletRequest request
    ) {
        PageRequest pageRequest = PageHelper.createRequest(from, size);
        var dateTimeNow = LocalDateTime.now();
        if (rangeStart == null) {
            rangeStart = dateTimeNow;
        }

        QEvent qModel = QEvent.event;
        BooleanExpression predicatePublic = qModel.eventDate.after(rangeStart).and(qModel.state.eq(EventState.PUBLISHED));
        if (rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new MainParameterException("Incorrect time");
            }
            predicatePublic = predicatePublic.and(qModel.eventDate.before(rangeEnd));
        }
        if (text != null) {
            predicatePublic = predicatePublic.and(qModel.annotation.containsIgnoreCase(text).or(qModel.description.containsIgnoreCase(text)));
        }
        if (categories != null) {
            predicatePublic = predicatePublic.and(qModel.category.id.in(categories));
        }
        if (paid != null) {
            predicatePublic = predicatePublic.and(qModel.paid.eq(paid));
        }
        List<Event> foundEvents = new ArrayList<>();
        eventRepository.findAll(predicatePublic).forEach(foundEvents::add);
        if (onlyAvailable) {
            foundEvents = foundEvents.stream()
                    .filter(e -> EventUtils.countConfirmedRequests(e) < e.getParticipantLimit())
                    .collect(Collectors.toList());
        }
        if (foundEvents.size() == 0) {
            return new ArrayList<>();
        } else {
            List<Event> eventsPageAndSort = new ArrayList<>();
            if (sort == null) {
                eventsPageAndSort = foundEvents.stream()
                        .skip(pageRequest.getOffset())
                        .limit(pageRequest.getPageSize())
                        .collect(Collectors.toList());
            }
            if (sort != null && sort.equals(EventSort.EVENT_DATE)) {
                eventsPageAndSort = foundEvents.stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .skip(pageRequest.getOffset())
                        .limit(pageRequest.getPageSize())
                        .collect(Collectors.toList());
            }
            if (sort != null && sort.equals(EventSort.VIEWS)) {
                eventsPageAndSort = foundEvents.stream()
                        .sorted(Comparator.comparing(this::getViews).reversed())
                        .skip(pageRequest.getOffset())
                        .limit(pageRequest.getPageSize())
                        .collect(Collectors.toList());
            }
            statsClient.saveStats("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), dateTimeNow);//?
            var result = EventConverter.mapToShortDto(eventsPageAndSort);
            setViewsForListShortDto(result);
            return result;
        }
    }

    public EventFullDto getEventByIdPublic(Long id, HttpServletRequest request) {
        Event foundEvent = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new MainNotFoundException("Event with id=" + id + " was not found"));

        var dateTimeNow = LocalDateTime.now();
        statsClient.saveStats("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), dateTimeNow);
        Long viewsFromStats = getViews(foundEvent);
        var result = EventConverter.convertToDtoFull(foundEvent);
        result.setViews(viewsFromStats);
        return result;
    }

    private Long getViews(Event event) {
        long id = event.getId();
        String[] uris = {"/events/" + id};
        List<StatsDto> stats = statsClient.getStats(event.getCreatedOn(), LocalDateTime.now(), List.of(uris), true);

        return stats
                .stream()
                .map(StatsDto::getHits)
                .findFirst()
                .orElse(0L);
    }

    private void setViewsForListShortDto(List<? extends EventShortDto> events) {
        if (events.size() != 0) {
            String[] uris = new String[events.size()];
            for (int i = 0; i < uris.length; i++) {
                long id = events.get(i).getId();
                uris[i] = "/events/" + id;
            }
            LocalDateTime start = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
            LocalDateTime end = LocalDateTime.now();
            try {
                List<StatsDto> stats = statsClient.getStats(start, end, List.of(uris), true);
                var mapUriToHits = stats.stream()
                        .filter(statsDto -> statsDto.getApp().equals("ewm-main-service"))
                        .collect(Collectors.toMap(StatsDto::getUri, StatsDto::getHits));
                for (int i = 0; i < uris.length; i++) {
                    events.get(i).setViews(mapUriToHits.getOrDefault(uris[i], 0L));
                }
            } catch (HttpClientErrorException.NotFound e) {
                log.info("Stats service: {}", e.getMessage());
                for (int i = 0; i < uris.length; i++) {
                    events.get(i).setViews(0L);
                }
            }
        }
    }
}