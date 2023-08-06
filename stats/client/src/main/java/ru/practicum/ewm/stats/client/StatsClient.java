package ru.practicum.ewm.stats.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stats.collective.HitDto;
import ru.practicum.ewm.stats.collective.StatsDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        HttpEntity<List<StatsDto>> requestEntity = new HttpEntity<>(defaultHeaders());

        if (uris != null && !uris.isEmpty()) {
            String urisString = String.join(",", uris);

            Map<String, Object> parameters = Map.of(
                    "start", encodeDateTime(start),
                    "end", encodeDateTime(end),
                    "uris", urisString,
                    "unique", unique);

            StatsDto[] response = rest.getForObject(
                    "/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                    StatsDto[].class,
                    parameters);

            return Objects.isNull(response) ? List.of() : List.of(response);
        }

        Map<String, Object> parameters = Map.of("start", encodeDateTime(start),
                "end", encodeDateTime(end),
                "unique", unique);

        StatsDto[] response = rest.getForObject(
                "/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                StatsDto[].class,
                parameters);

        return Objects.isNull(response) ? List.of() : List.of(response);

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