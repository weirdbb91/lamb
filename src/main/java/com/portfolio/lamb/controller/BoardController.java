package com.portfolio.lamb.controller;

import com.portfolio.lamb.domain.Board;
import com.portfolio.lamb.domain.BoardDto;
import com.portfolio.lamb.domain.Member;
import com.portfolio.lamb.domain.content.MembersContent;
import com.portfolio.lamb.service.BoardService;
import com.portfolio.lamb.validator.BoardDtoValidator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/board")
public class BoardController extends BaseRestController<BoardDto, BoardService, BoardDtoValidator> {


    @GetMapping("/")
    public String index() {
        return "board";
    }

    @Override
    protected String getThisTypeName() {
        return "board";
    }

    @Override
    protected MembersContent getNewContent(Member member) {
        return new Board(member);
    }
}