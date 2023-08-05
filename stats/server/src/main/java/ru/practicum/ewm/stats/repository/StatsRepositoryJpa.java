package ru.practicum.ewm.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.ewm.stats.collective.StatsDto;
import ru.practicum.ewm.stats.model.HitModel;

import java.time.LocalDateTime;
import java.util.List;

@EnableJpaRepositories
public interface StatsRepositoryJpa extends JpaRepository<HitModel, Long> {

    @Query(value = "select application, uri, count(remote_ip) as countIp " +
            "from hits where (date_time between :start and :end) " +
            "group by application, uri order by countIp desc", nativeQuery = true)
    List<StatsDto> findAll(LocalDateTime start, LocalDateTime end);

    @Query(value = "select application, uri, count(distinct remote_ip) as countIp " +
            "from hits where (date_time between :start and :end) " +
            "group by application, uri order by countIp desc", nativeQuery = true)
    List<StatsDto> findAllUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query(value = "select application, uri, count(remote_ip) as countIp " +
            "from hits where uri in :uris " +
            "and (date_time between :start and :end) " +
            "group by application, uri order by countIp desc", nativeQuery = true)
    List<StatsDto> findStatsByUris(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query(value = "select application, uri, count(distinct remote_ip) as countIp " +
            "from hits where uri in :uris " +
            "and (date_time between :start and :end) " +
            "group by application, uri order by countIp desc", nativeQuery = true)
    List<StatsDto> findStatsByUrisUniqueIp(LocalDateTime start, LocalDateTime end, String[] uris);
}