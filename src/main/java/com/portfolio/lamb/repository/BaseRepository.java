package com.portfolio.lamb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    Page<T> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    List<T> findByIsEnabled();

    Optional<T> findByTitle(String title);
}
