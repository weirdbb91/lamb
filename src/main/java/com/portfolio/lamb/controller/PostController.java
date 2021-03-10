package com.portfolio.lamb.controller;

import com.portfolio.lamb.domain.Member;
import com.portfolio.lamb.domain.Post;
import com.portfolio.lamb.domain.PostDto;
import com.portfolio.lamb.domain.content.MembersContent;
import com.portfolio.lamb.service.PostService;
import com.portfolio.lamb.validator.PostDtoValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/post")
public class PostController extends BaseRestController<PostDto, PostService, PostDtoValidator> {

    @Override
    protected String getThisTypeName() {
        return "post";
    }

    @Override
    protected MembersContent getNewContent(Member member) {
        return new Post(member);
    }



    @GetMapping("/view")
    public String view(Model model) {
        model.addAttribute("postDto", new PostDto(77L));
        return "post/view";
    }
}
