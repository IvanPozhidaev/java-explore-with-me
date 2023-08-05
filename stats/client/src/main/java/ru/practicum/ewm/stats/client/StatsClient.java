package ru.practicum.ewm.stats.client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stats.collective.HitDto;
import ru.practicum.ewm.stats.collective.StatsDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsClient {

    private final RestTemplate rest;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(RestTemplate rest) {
        this.rest = rest;
    }


    public void saveStats(String app, String uri, String ip, LocalDateTime dateTime) {
        HitDto body = new HitDto(app, uri, ip, dateTime);
        rest.postForEntity("/hit", body, Void.class);
    }

    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, @Nullable String[] uris, @Nullable boolean unique) {
        HttpEntity<List<StatsDto>> requestEntity = new HttpEntity<>(defaultHeaders());
        if (uris != null) {
            Map<String, Object> parametersUris = Map.of("start", encodeDateTime(start), "end", encodeDateTime(end), "uris", uris, "unique", unique);
            ResponseEntity<StatsDto[]> response = rest.exchange("/stats?start={start}&end={end}&uris={uris}&unique={unique}", HttpMethod.GET, requestEntity, StatsDto[].class, parametersUris);
            StatsDto[] result = response.getBody();
            return Arrays.stream(result).collect(Collectors.toList());
        } else {
            Map<String, Object> parameters = Map.of("start", encodeDateTime(start), "end", encodeDateTime(end), "unique", unique);
            ResponseEntity<StatsDto[]> response = rest.exchange("/stats?start={start}&end={end}&uris={uris}&unique={unique}", HttpMethod.GET, requestEntity, StatsDto[].class, parameters);
            StatsDto[] result = response.getBody();
            return Arrays.stream(result).collect(Collectors.toList());
        }
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders head = new HttpHeaders();
        head.setContentType(MediaType.APPLICATION_JSON);
        head.setAccept(List.of(MediaType.APPLICATION_JSON));
        return head;
    }

    private String encodeDateTime(LocalDateTime dateTime) {
        String dateTimeString = dateTime.format(formatter);
        return URLEncoder.encode(dateTimeString, StandardCharsets.UTF_8);
    }
}