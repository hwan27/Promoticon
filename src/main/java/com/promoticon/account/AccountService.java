package com.promoticon.account;

import com.promoticon.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    public void processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .username(signUpForm.getUsername())
                .password(signUpForm.getPassword()) //TODO password encoding
                .emoticonCreatedByWeb(true)
                .feedbackUpdatedByWeb(true)
                .build();

        Account newAccount = accountRepository.save(account);
        return newAccount;
    }

    private void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("Promoticon: 회원 가입 인");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken()
                + "&email=" + newAccount.getEmail());

        javaMailSender.send(mailMessage);
    }
}
