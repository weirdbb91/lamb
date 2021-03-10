package com.portfolio.lamb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T> extends JpaRepository<T, Long> {

    Page<T> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    Optional<T> findByTitle(String title);
}
