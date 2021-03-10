package com.portfolio.lamb.service;

import com.portfolio.lamb.domain.content.IContent;
import com.portfolio.lamb.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BaseService<T extends IContent> {

    Page<T> getFilteredPage(String searchText, Pageable pageable);

    List<T> getList();

    Page<T> getPage(Pageable pageable);

    Optional<T> getById(Long id);

    Optional<T> getByTitle(String title);

    T save(T post);

    void deleteById(Long id);
}
