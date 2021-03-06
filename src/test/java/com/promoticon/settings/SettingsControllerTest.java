package com.promoticon.settings;

import com.promoticon.WithAccount;
import com.promoticon.account.AccountRepository;
import com.promoticon.account.AccountService;
import com.promoticon.account.SignUpForm;
import com.promoticon.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

//    @BeforeEach
//    void beforeEach() {
//        SignUpForm signUpForm = new SignUpForm();
//        signUpForm.setUsername("mea");
//        signUpForm.setEmail("mea@gmail.com");
//        signUpForm.setPassword("12341234");
//        accountService.processNewAccount(signUpForm);
//    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("mea")
    @DisplayName("프로필 수정 폼")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    //@WithUserDetails(value = "mea", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @WithAccount("mea")
    @DisplayName("프로필 수정하기: 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account mea = accountRepository.findByUsername("mea");
        assertEquals(bio, mea.getBio());
    }

    @WithAccount("mea")
    @DisplayName("프로필 수정하기: 입력값 에러")
    @Test
    void updateProfileWithError() throws Exception {
        String bio = "너무 길게 소개를 수정하는 경우너무 길게 소개를 수정하는 경우너무 길게 소개를 수정하는 경우너무 길게 소개를 수정하는 경우";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account mea = accountRepository.findByUsername("mea");
        assertNull(mea.getBio());
    }

    @WithAccount("mea")
    @DisplayName("패스워드 수정 폼")
    @Test
    void updatePassword_form() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("mea")
    @DisplayName("패스워드 수정: 입력값 정상")
    @Test
    void updatePassword_success() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                .param("newPassword", "12341234")
                .param("newPasswordConfirm", "12341234")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account mea = accountRepository.findByUsername("mea");
        assertTrue(passwordEncoder.matches("12341234", mea.getPassword()));
    }

    @WithAccount("mea")
    @DisplayName("패스워드 수정 실패: 패스워드 불일치")
    @Test
    void updatePasswordWithError() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                .param("newPassword", "12341234")
                .param("newPasswordConfirm", "11111111")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }

    @WithAccount("mea")
    @DisplayName("유저이름 수정 폼")
    @Test
    void updateUsernameForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_ACCOUNT_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("usernameForm"));
    }


    @WithAccount("mea")
    @DisplayName("유저이름 변경 성공")
    @Test
    void updateUsername() throws Exception {
        String newUsername = "hwan";
        mockMvc.perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
                .param("username", newUsername)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_ACCOUNT_URL))
                .andExpect(flash().attributeExists("message"));
        assertNotNull(accountRepository.findByUsername("hwan"));
    }

    @WithAccount("mea")
    @DisplayName("유저이름 변경 실패: 입력값 에러")
    @Test
    void updateUsernameWithWrongInput() throws Exception {
        String newUsername = "me_/";
        mockMvc.perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
                        .param("username", newUsername)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_ACCOUNT_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("usernameForm"));
    }
}