package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.ewm.model.UserModel;

@EnableJpaRepositories
public interface UserRepository extends JpaRepository<UserModel, Long> {

    @Query(value = "select * from users where id in :ids", nativeQuery = true)
    Page<UserModel> findAllIds(Long[] ids, PageRequest pageRequest);
}