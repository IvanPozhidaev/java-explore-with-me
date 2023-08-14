package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.ewm.entity.Comment;

@EnableJpaRepositories
public interface CommentRepository extends JpaRepository<Comment, Long> {

}