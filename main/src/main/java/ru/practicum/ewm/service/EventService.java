package ru.practicum.ewm.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.ewm.converter.CategoryConverter;
import ru.practicum.ewm.converter.CommentConverter;
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
import ru.practicum.ewm.repository.*;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.collective.StatsDto;
import ru.practicum.ewm.util.EventUtils;
import ru.practicum.ewm.util.PageHelper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private static final Integer HOURS_BEFORE_START_EVENT_ADMIN = 1;
    private static final Integer HOURS_BEFORE_START_EVENT_USER = 2;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;
    private final CategoryService categoryService;
    private final CommentRepository commentRepository;

    public EventFullDto addEvent(Long userId, EventDto eventDto) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new MainNotFoundException(String.format("User with id=%s was not found", userId)));

        var minStartDate = LocalDateTime.now().plusHours(HOURS_BEFORE_START_EVENT_USER);
        if (eventDto.getEventDate().isBefore(minStartDate)) {
            throw new MainParamConflictException(String.format("Event date must be not earlier than %s hours later",
                    HOURS_BEFORE_START_EVENT_USER));
        }

        var createdEvent = EventConverter.convertToModel(user, eventDto);
        var category = CategoryConverter.convertToModel(categoryService.getCategoryById(eventDto.getCategory()));
        createdEvent.setCategory(category);
        createdEvent.setCreatedOn(LocalDateTime.now());
        createdEvent.setState(EventState.PENDING);

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
        return result.size() == 0 ? Collections.emptyList() : EventConverter.mapToShortDto(result);
    }

    public EventFullDto getEventByIdPrivate(Long userId, Long eventId) {
        var foundEvent = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new MainNotFoundException(String.format(
                        "Event with id=%s and added by user id=%s was not found", eventId, userId)));

        var result = EventConverter.convertToDtoFull(foundEvent);
        result.setViews(getViews(foundEvent));

        var comments = commentRepository.findAllByEventId(eventId);
        result.setComments(CommentConverter.mapToDto(comments));

        return result;
    }

    public EventFullDto updateEventPrivate(Long userId, Long eventId, EventUpdateDto eventDto) {
        var eventToUpd = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new MainNotFoundException(String.format(
                        "Event with id=%s and added by user id=%s was not found", eventId, userId)));

        if (eventToUpd.getState().equals(EventState.PUBLISHED)) {
            throw new MainParamConflictException("Updated event must be not published");
        }

        if (eventDto.getEventDate() != null) {
            var minStartDate = LocalDateTime.now().plusHours(HOURS_BEFORE_START_EVENT_USER);
            if (eventDto.getEventDate().isBefore(minStartDate)) {
                throw new MainParamConflictException(String.format("Event date must be not earlier than %s hours later",
                        HOURS_BEFORE_START_EVENT_USER));
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
        eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new MainNotFoundException(String.format(
                        "Event with id=%s and added by user id=%s was not found", eventId, userId)));

        var listRequests = requestRepository.findAllByEventId(eventId);
        return RequestConverter.mapToDto(listRequests);
    }

    public RequestUpdateResultDto updateStatusRequestsForEvent(Long userId, Long eventId, RequestUpdateDto requestDto) {
        var event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new MainNotFoundException(
                        String.format("Event with id=%s and added by user id=%s was not found", eventId, userId)));

        int confirmedRequests = requestRepository.countConfirmedByEventId(event.getId());
        if (confirmedRequests > event.getParticipantLimit()) {
            throw new MainParamConflictException("The limit of participation in the event has been reached");
        }

        RequestUpdateResultDto afterUpdateStatus = new RequestUpdateResultDto();

        var allRequests = requestRepository.findAllById(requestDto.getRequestIds()).stream()
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

            for (Request request : selectedRequests) {
                if (event.getParticipantLimit() > confReq) {
                    if (requestDto.getStatus().equals(RequestUpdateStatus.CONFIRMED)) {
                        request.setStatus(RequestStatus.CONFIRMED);
                        confReq++;
                        if (event.getParticipantLimit() == confReq) {
                            if (event.getParticipantLimit() == confReq) {
                                event.getAllRequests()
                                        .stream()
                                        .filter(rm -> rm.getStatus().equals(RequestStatus.PENDING))
                                        .forEach(rm -> rm.setStatus(RequestStatus.REJECTED));
                            }
                        }
                        afterUpdateStatus.getConfirmedRequests().add(RequestConverter.convertToDto(request));
                    }
                    if (requestDto.getStatus().equals(RequestUpdateStatus.REJECTED)) {
                        request.setStatus(RequestStatus.REJECTED);
                        afterUpdateStatus.getRejectedRequests().add(RequestConverter.convertToDto(request));
                    }
                } else {
                    if (requestDto.getStatus().equals(RequestUpdateStatus.CONFIRMED)) {
                        throw new MainParamConflictException("The participant limit has been reached");
                    }
                }
            }
        }
        requestRepository.saveAll(selectedRequests);
        return afterUpdateStatus;
    }


    public List<EventFullDto> searchEventsAdmin(Long[] users, List<EventState> states, Long[] categories,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
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

        List<Event> foundEventsAdmin = eventRepository.findAll(predicateAdmin, pageRequest).toList();

        List<Request> requestCountDtos = requestRepository.findConfirmedByEventIdIn(foundEventsAdmin.stream()
                .map(Event::getId)
                .collect(Collectors.toList()));

        Map<Long, Long> collect = requestCountDtos.stream()
                .filter(request -> request.getStatus().equals(RequestStatus.CONFIRMED))
                .collect(Collectors.groupingBy(request -> request.getEvent().getId()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Long.valueOf(e.getValue().size())));

        if (!foundEventsAdmin.isEmpty()) {
            var result = EventConverter.mapToDtoFull(foundEventsAdmin);
            setViewsForListShortDto(result);

            result.forEach(eventFullDto -> {
                Long count = collect.getOrDefault(eventFullDto.getId(), 0L);
                eventFullDto.setConfirmedRequests(count);
            });
            return result;
        }

        return Collections.emptyList();
    }

    public EventFullDto updateEventByAdmin(Long eventId, EventUpdateDto eventDto) {
        var eventToUpdAdmin = eventRepository.findById(eventId).orElseThrow(() ->
                new MainNotFoundException(String.format("Event with id=%s was not found", eventId)));

        if (eventDto.getEventDate() != null) {
            var minStartDate = LocalDateTime.now().plusHours(HOURS_BEFORE_START_EVENT_ADMIN);
            if (eventDto.getEventDate().isBefore(minStartDate)) {
                throw new MainParamConflictException(String.format("Event date must be not earlier than %s hours later",
                        HOURS_BEFORE_START_EVENT_ADMIN));
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
                var minStartDate = datePublish.plusHours(HOURS_BEFORE_START_EVENT_ADMIN);
                if (eventToUpdAdmin.getEventDate().isBefore(minStartDate)) {
                    throw new MainParamConflictException(
                            String.format("Event date must be not earlier than %s hour before published",
                                    HOURS_BEFORE_START_EVENT_ADMIN));
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

    public List<EventShortDto> getEventsPublic(EventSearchDto request, String ip, String uri) {
        PageRequest pageRequest = PageHelper.createRequest(request.getFrom(), request.getSize());
        var dateTimeNow = LocalDateTime.now();

        if (request.getRangeStart() == null) {
            request.setRangeStart(dateTimeNow);
        }

        QEvent qModel = QEvent.event;
        BooleanExpression predicatePublic = qModel.eventDate.after(request.getRangeStart()).and(qModel.state.eq(EventState.PUBLISHED));
        if (request.getRangeEnd() != null) {
            if (request.getRangeStart().isAfter(request.getRangeEnd())) {
                throw new MainParameterException("Incorrect time");
            }
            predicatePublic = predicatePublic.and(qModel.eventDate.before(request.getRangeEnd()));
        }
        if (request.getText() != null) {
            predicatePublic = predicatePublic
                    .and(qModel.annotation.containsIgnoreCase(request.getText())
                            .or(qModel.description.containsIgnoreCase(request.getText())));
        }
        if (request.getCategories() != null) {
            predicatePublic = predicatePublic.and(qModel.category.id.in(request.getCategories()));
        }
        if (request.getPaid() != null) {
            predicatePublic = predicatePublic.and(qModel.paid.eq(request.getPaid()));
        }

        List<Event> foundEvents = new ArrayList<>();
        eventRepository.findAll(predicatePublic).forEach(foundEvents::add);
        if (request.getOnlyAvailable()) {
            foundEvents = foundEvents.stream()
                    .filter(e -> EventUtils.countConfirmedRequests(e) < e.getParticipantLimit())
                    .collect(Collectors.toList());
        }
        if (foundEvents.isEmpty()) {
            return Collections.emptyList();
        }

        List<Event> eventsPageAndSort = new ArrayList<>();
        if (request.getSort() == null) {
            eventsPageAndSort = foundEvents.stream()
                    .skip(pageRequest.getOffset())
                    .limit(pageRequest.getPageSize())
                    .collect(Collectors.toList());
        }
        if (request.getSort() != null && request.getSort().equals(EventSort.EVENT_DATE)) {
            eventsPageAndSort = foundEvents.stream()
                    .sorted(Comparator.comparing(Event::getEventDate))
                    .skip(pageRequest.getOffset())
                    .limit(pageRequest.getPageSize())
                    .collect(Collectors.toList());
        }

        var result = EventConverter.mapToShortDto(eventsPageAndSort);

        setViewsForListShortDto(result);
        statsClient.saveStats("ewm-main-service", uri, ip, dateTimeNow);

        if (request.getSort() != null && request.getSort().equals(EventSort.VIEWS)) {
            return result.stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews).reversed())
                    .skip(pageRequest.getOffset())
                    .limit(pageRequest.getPageSize())
                    .collect(Collectors.toList());
        }
        return result;
    }

    public EventFullDto getEventByIdPublic(Long id, String uri, String ip) {
        Event foundEvent = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new MainNotFoundException(String.format("Event with id=%s was not found", id)));

        var dateTimeNow = LocalDateTime.now();
        statsClient.saveStats("ewm-main-service", uri, ip, dateTimeNow);
        Long viewsFromStats = getViews(foundEvent);
        var result = EventConverter.convertToDtoFull(foundEvent);
        result.setViews(viewsFromStats);

        var comments = commentRepository.findTop10ByEventIdOrderByCreatedDesc(foundEvent.getId());
        result.setComments(CommentConverter.mapToDto(comments));

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