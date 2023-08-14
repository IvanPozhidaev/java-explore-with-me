package ru.practicum.ewm.stats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.collective.HitDto;
import ru.practicum.ewm.stats.collective.StatsDto;
import ru.practicum.ewm.stats.converter.ConverterModelDto;
import ru.practicum.ewm.stats.entity.Hit;
import ru.practicum.ewm.stats.exception.ParameterException;
import ru.practicum.ewm.stats.repository.StatsRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsService {

    private final StatsRepositoryJpa statsRepository;

    @Autowired
    public StatsService(StatsRepositoryJpa statsRepository) {
        this.statsRepository = statsRepository;
    }

    public void addHit(HitDto hitDto) {
        Hit addedHit = ConverterModelDto.convertToModel(hitDto);
        statsRepository.save(addedHit);
    }

    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean uniq) {
        checkStartIsAfterEnd(start, end);

        if (uniq) {
            if (uris == null) {
                return statsRepository.findAllUniqueIp(start, end);
            }
            return statsRepository.findStatsByUrisUniqueIp(start, end, uris);
        } else {
            if (uris == null) {
                return statsRepository.findAll(start, end);
            }
            return statsRepository.findAllByUris(start, end, uris);
        }
    }

    private void checkStartIsAfterEnd(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new ParameterException("Start date must be before end date");
        }
    }
}