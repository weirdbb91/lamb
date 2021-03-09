package com.portfolio.lamb.validator;

import com.portfolio.lamb.domain.MemberContentDto;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public abstract class MemberContentDtoValidator<T extends MemberContentDto> implements Validator {

    protected final String DEFAULT = "error";
    protected final String TITLE = "title";
    protected final String CONTENT = "content";

    protected final String CODE_NOT_FOUND = "NOT_FOUND";
    protected final String MSG_NOT_FOUND = "not found";

    protected final String CODE_VALUE_EMPTY = "EMPTY";
    protected final String MSG_VALUE_EMPTY = "value is empty";

    protected final String CODE_SIZE_LONG = "SIZE_L";
    protected final String MSG_SIZE_LONG = "too long size";


    protected abstract int getTitleMaxSize();

    protected abstract int getContentMaxSize();

    protected void validateTitleAndContent(Object object, Errors errors) {

        T obj = (T) object;

        if (obj == null) {
            errors.rejectValue(DEFAULT, CODE_NOT_FOUND, MSG_NOT_FOUND);
            return;
        }

        String title = obj.getTitle();

        if (isThisEmpty(title))
            errors.rejectValue(TITLE, CODE_VALUE_EMPTY, MSG_VALUE_EMPTY);

        if (title.length() > getTitleMaxSize())
            errors.rejectValue(TITLE, CODE_SIZE_LONG, MSG_SIZE_LONG);


        String content = obj.getContent();

        if (content.length() > getContentMaxSize())
            errors.rejectValue(CONTENT, CODE_SIZE_LONG, MSG_SIZE_LONG);
    }

    protected boolean isThisEmpty(String str) {
        return ("".equals(str)) || (str.length() == 0);
    }
}
