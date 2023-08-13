package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.ewm.model.LocationModel;

import java.util.List;

@EnableJpaRepositories
public interface LocationRepository extends JpaRepository<LocationModel, Long> {

    List<LocationModel> findByLatAndLon(float lat, float lon);
}