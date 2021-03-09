package com.portfolio.lamb.validator;

import com.portfolio.lamb.domain.board.BoardDto;
import com.portfolio.lamb.domain.board.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class BoardValidator implements Validator {

    @Autowired
    private BoardService boardService;


    @Override
    public boolean supports(Class<?> clazz) {
        return BoardDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        BoardDto boardDto = (BoardDto) target;
        if (boardDto == null) {
            errors.rejectValue("board", "BOARD_NOT_FOUND", "board not found");
            return;
        }
        if (!StringUtils.hasLength(boardDto.getTitle())) {
            errors.rejectValue("title", "TITLE_NOTHING", "input title");
        }
        if (boardDto.getTitle().length() > 15) {
            errors.rejectValue("title", "TITLE_TOO_LONG", "title is too long");
        }
        if (boardDto.getContent().length() > 1000) {
            errors.rejectValue("content", "CONTENT_TOO_LONG", "content is too long");
        }

        if (boardDto.getId() != 0L &&
                boardDto.getCompareTitle().equals(boardDto.getTitle())) {
            return;
        }

        if (boardService.getBoardByTitle(boardDto.getTitle()).isPresent()) {
            errors.rejectValue("title", "TITLE_DUP",
                    "the title name is already using");
        }

    }
}
