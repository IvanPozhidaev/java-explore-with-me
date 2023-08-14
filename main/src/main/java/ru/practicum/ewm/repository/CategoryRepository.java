package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.ewm.entity.Category;

@EnableJpaRepositories
public interface CategoryRepository extends JpaRepository<Category, Long> {
}