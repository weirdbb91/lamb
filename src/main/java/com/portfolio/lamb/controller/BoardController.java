package com.portfolio.lamb.controller;

import com.portfolio.lamb.domain.Board;
import com.portfolio.lamb.domain.BoardDto;
import com.portfolio.lamb.domain.Member;
import com.portfolio.lamb.domain.content.IContent;
import com.portfolio.lamb.service.BoardService;
import com.portfolio.lamb.validator.BoardDtoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board")
public class BoardController extends BaseController<Board, BoardDto, BoardService, BoardDtoValidator> {

    @Override
    protected String getThisTypeName() {
        return "board";
    }

    @Override
    protected IContent getNewContent(Member member) {
        return new Board(member);
    }
}