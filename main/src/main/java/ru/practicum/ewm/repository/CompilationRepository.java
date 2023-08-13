package ru.practicum.ewm.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.ewm.model.CompilationModel;

@EnableJpaRepositories
public interface CompilationRepository extends JpaRepository<CompilationModel, Long> {

    Page<CompilationModel> findAllByPinned(Boolean pinned, PageRequest pageRequest);
}