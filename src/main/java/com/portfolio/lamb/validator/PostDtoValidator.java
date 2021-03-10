package com.portfolio.lamb.validator;

import com.portfolio.lamb.domain.PostDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class PostDtoValidator extends ContentDtoValidator<PostDto> implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PostDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        validateTitleAndContent(target, errors);
    }
}
