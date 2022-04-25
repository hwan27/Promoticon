package com.promoticon.settings.validator;

import com.promoticon.account.AccountRepository;
import com.promoticon.domain.Account;
import com.promoticon.settings.form.UsernameForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class UsernameValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return UsernameForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UsernameForm usernameForm = (UsernameForm) target;
        Account byUsername = accountRepository.findByUsername(usernameForm.getUsername());
        if (byUsername != null) {
            errors.rejectValue("username", "wrong.value", "입력하신 유저네임이 이미 존재합니다.");
        }
    }
}
