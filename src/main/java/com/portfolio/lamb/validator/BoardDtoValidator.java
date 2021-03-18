package com.portfolio.lamb.validator;

import com.portfolio.lamb.domain.BoardDto;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;


@Component
public class BoardDtoValidator extends ContentDtoValidator<BoardDto> {

    @Override
    public boolean supports(Class<?> clazz) {
        return BoardDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        validateTitleAndContent(target, errors);
    }
}
