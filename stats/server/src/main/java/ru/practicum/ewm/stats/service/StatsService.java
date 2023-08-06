package ru.practicum.ewm.stats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.collective.HitDto;
import ru.practicum.ewm.stats.collective.StatsDto;
import ru.practicum.ewm.stats.model.ConverterModelDto;
import ru.practicum.ewm.stats.model.HitModel;
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
        HitModel addedHitModel = ConverterModelDto.convertToModel(hitDto);
        statsRepository.save(addedHitModel);
    }

    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean uniq) {
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
}