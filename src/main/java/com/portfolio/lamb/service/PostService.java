package com.portfolio.lamb.service;

import com.portfolio.lamb.domain.Post;
import com.portfolio.lamb.repository.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class PostService extends BaseService<Post, PostRepository> {
}
