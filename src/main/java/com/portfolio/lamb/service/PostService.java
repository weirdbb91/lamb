package com.portfolio.lamb.service;

import com.portfolio.lamb.domain.Post;
import com.portfolio.lamb.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService implements BaseService<Post> {

    @Autowired
    private PostRepository repo;

    public Page<Post> getFilteredPage(String searchText, Pageable pageable) {
        return repo.findByTitleContainingOrContentContaining(searchText, searchText, pageable);
    }

    public List<Post> getList() {
        return repo.findAll();
    }

    public Page<Post> getPage(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Optional<Post> getById(Long id) {
        return repo.findById(id);
    }

    public Optional<Post> getByTitle(String title) {
        return repo.findByTitle(title);
    }

    public Post save(Post post) {
        return repo.save(post);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
