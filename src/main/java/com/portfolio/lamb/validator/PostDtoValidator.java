package com.portfolio.lamb.validator;

import com.portfolio.lamb.domain.PostDto;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;


@Component
public class PostDtoValidator extends ContentDtoValidator<PostDto> {

    @Override
    public boolean supports(Class<?> clazz) {
        return PostDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        validateTitleAndContent(target, errors);
    }
}
