package com.portfolio.lamb.validator;

import com.portfolio.lamb.config.CustomClientRegistrationRepository;
import com.portfolio.lamb.domain.MemberDto;
import com.portfolio.lamb.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class MemberDtoValidator implements Validator {

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> clazz) {
        return MemberDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        errors.rejectValue("unknown", "UNKNOWN", "unknown input error");
    }


    public void validateUsername(Object target, Errors errors) {
        String field = "username";
        MemberDto memberDto = (MemberDto) target;
        String value = memberDto.getUsername();

        if (isEmpty(field, value, errors)) return;
        uniqueName(field, value, errors);
    }

    public void validateNickname(Object target, Errors errors) {
        String field = "nickname";
        MemberDto memberDto = (MemberDto) target;
        String value = memberDto.getNickname();

        if (isEmpty(field, value, errors)) return;
        if (value.equals(memberDto.getUsername())) return;
        uniqueName(field, value, errors);
    }

    public void passwordCheck(Object target, Errors errors) {
        MemberDto memberDto = (MemberDto) target;
        String value = memberDto.getPassword();

        if (isEmpty("password", value, errors)) return;
        if (isEmpty("comparePassword", memberDto.getComparePassword(), errors)) return;
        if (!passwordEncoder.matches(value, memberDto.getComparePassword())) {
            errors.rejectValue("password", "PASSWORD_WRONG",
                    "wrong password");
        }
    }

    public boolean validConfirm(Object value, Errors errors) {
        MemberDto memberDto = (MemberDto) value;
        if (isEmpty("comparePassword", memberDto.getComparePassword(), errors)) return false;
        if (isEmpty("confirm", memberDto.getConfirm(), errors)) return false;

        if (!memberDto.getComparePassword().equals(memberDto.getConfirm())) {
            errors.rejectValue("confirm", "DIFFERENT_CONFIRM",
                    "new password and confirm are different");
            return false;
        }
        return true;
    }

    private void uniqueName(String field, String value, Errors errors) {
        if (CustomClientRegistrationRepository.clients.stream().anyMatch(value::startsWith)) {
            errors.rejectValue(field, field.toUpperCase() + "_PREFIX_CLIENT",
                    "can not start with social client's name");
        }
        if (!memberService.isUniqueUsername(value) || !memberService.isUniqueNickname(value)) {
            errors.rejectValue(field, field.toUpperCase() + "_DUP",
                    "someone is already using " + value);
        }
    }

    private boolean isEmpty(String field, String value, Errors errors) {
        if (!StringUtils.hasLength(value)) {
            errors.rejectValue(field, field.toUpperCase() + "_EMPTY",
                    field + " is empty");
            return true;
        }
        return false;
    }
}
