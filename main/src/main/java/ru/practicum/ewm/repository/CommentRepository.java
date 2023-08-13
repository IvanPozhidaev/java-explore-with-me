package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.ewm.model.CommentModel;

@EnableJpaRepositories
public interface CommentRepository extends JpaRepository<CommentModel, Long> {

}