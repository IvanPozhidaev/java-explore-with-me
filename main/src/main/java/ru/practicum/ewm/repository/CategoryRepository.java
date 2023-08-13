package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.ewm.model.CategoryModel;

@EnableJpaRepositories
public interface CategoryRepository extends JpaRepository<CategoryModel, Long> {

}