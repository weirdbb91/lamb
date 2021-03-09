package com.portfolio.lamb.validator;

import com.portfolio.lamb.domain.board.BoardDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class BoardDtoValidator extends MemberContentDtoValidator<BoardDto> implements Validator {

    @Override
    protected int getTitleMaxSize() {
        return 15;
    }

    @Override
    protected int getContentMaxSize() {
        return 1000;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return BoardDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validateTitleAndContent(target, errors);
    }
}
