package com.portfolio.lamb.service;

import com.portfolio.lamb.domain.content.IContent;
import com.portfolio.lamb.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public abstract class BaseService<T extends IContent, R extends BaseRepository<T>> {

    R repo;

    public Page<T> getFilteredPage(String searchText, Pageable pageable) {
        return repo.findByTitleContainingOrContentContaining(searchText, searchText, pageable);
    }

    public List<T> getList() {
        return repo.findAll();
    }

    public Page<T> getPage(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Optional<T> getById(Long id) {
        return repo.findById(id);
    }

    public Optional<T> getByTitle(String title) {
        return repo.findByTitle(title);
    }

    public T save(T post) {
        return (T) repo.save(post);
    }

    public Long deleteById(Long id) {
        repo.deleteById(id);
        return id;
    }
}
