package com.portfolio.lamb.controller;

import com.portfolio.lamb.domain.Member;
import com.portfolio.lamb.domain.Post;
import com.portfolio.lamb.domain.PostDto;
import com.portfolio.lamb.service.PostService;
import com.portfolio.lamb.validator.PostDtoValidator;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/post")
public class PostController extends BaseController<Post, PostDto, PostService, PostDtoValidator> {
    @Override
    protected String getThisTypeName() {
        return "post";
    }

    @Override
    protected Post getNewContent(Member member) {
        return new Post(member);
    }

    @Override
    protected PostDto getNewDto() {
        return new PostDto();
    }
}