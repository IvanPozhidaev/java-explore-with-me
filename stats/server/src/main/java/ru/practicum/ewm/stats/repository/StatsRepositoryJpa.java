package ru.practicum.ewm.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.ewm.stats.collective.StatsDto;
import ru.practicum.ewm.stats.entity.Hit;

import java.time.LocalDateTime;
import java.util.List;

@EnableJpaRepositories
public interface StatsRepositoryJpa extends JpaRepository<Hit, Long> {

    @Query("select new ru.practicum.ewm.stats.collective.StatsDto(h.app, h.uri, count(h.ip)) " +
            "from Hit h " +
            "where h.dateTime between ?1 and ?2 " +
            "group by h.app, h.uri " +
            "order by count(h.ip) desc")
    List<StatsDto> findAll(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ewm.stats.collective.StatsDto(h.app, h.uri, count(distinct h.ip)) " +
            "from Hit h " +
            "where h.dateTime between ?1 and ?2 " +
            "and h.uri in (?3) " +
            "group by h.app, h.uri " +
            "order by count(distinct h.ip) desc")
    List<StatsDto> findAllUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ewm.stats.collective.StatsDto(h.app, h.uri, count(h.ip)) " +
            "from Hit h " +
            "where h.dateTime between ?1 and ?2 " +
            "and h.uri in (?3) " +
            "group by h.app, h.uri " +
            "order by count(h.ip) desc")
    List<StatsDto> findAllByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.ewm.stats.collective.StatsDto(h.app, h.uri, count(distinct h.ip)) " +
            "from Hit h " +
            "where h.dateTime between ?1 and ?2 " +
            "and h.uri in (?3) " +
            "group by h.app, h.uri " +
            "order by count(distinct h.ip) desc")
    List<StatsDto> findStatsByUrisUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);
}