package com.portfolio.lamb.service;

import com.portfolio.lamb.domain.Board;
import com.portfolio.lamb.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BoardService implements BaseService<Board> {

    @Autowired
    private BoardRepository repo;

    public Page<Board> getFilteredPage(String searchText, Pageable pageable) {
        return repo.findByTitleContainingOrContentContaining(searchText, searchText, pageable);
    }

    public List<Board> getList() {
        return repo.findAll();
    }

    public Page<Board> getPage(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Optional<Board> getById(Long id) {
        return repo.findById(id);
    }

    public Optional<Board> getByTitle(String title) {
        return repo.findByTitle(title);
    }

    public Board save(Board post) {
        return repo.save(post);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
