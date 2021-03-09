package com.portfolio.lamb.service;

import com.portfolio.lamb.domain.MemberContent;
import com.portfolio.lamb.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public abstract class BaseService<T extends MemberContent, ID, R extends BaseRepository> {

    R baseRepository;

    public T save(T post) {
        return (T) baseRepository.save(post);
    }

    public Optional<T> getById(long id) {
        return baseRepository.findById(id);
    }

    public List<T> getList() {
        return baseRepository.findByIsEnabled();
    }

    public long delete(long id) {
        baseRepository.delete(id);
        return id;
    }


    public Page<T> getPage(Pageable pageable) {
        return baseRepository.findAll(pageable);
    }

    public Page<T> getFilteredPage(String searchText, Pageable pageable) {
        return baseRepository.findByTitleContainingOrContentContaining(searchText, searchText, pageable);
    }

    public Optional<T> getByTitle(String title) {
        return baseRepository.findByTitle(title);
    }
}
