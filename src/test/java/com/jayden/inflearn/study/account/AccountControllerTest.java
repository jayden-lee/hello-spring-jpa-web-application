package com.jayden.inflearn.study.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private JavaMailSender javaMailSender;

    @DisplayName("회원 가입 페이지 요청 테스트")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
            .andExpect(status().isOk())
            .andExpect(view().name("account/sign-up"))
            .andExpect(model().attributeExists("signUpForm"));
    }

    @DisplayName("회원 가입 처리 - 입력값 오류")
    @Test
    void signUpFormSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
            .param("nickname", "jayden")
            .param("email", "email")
            .param("password", "12345")
            .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("account/sign-up"));
    }

    @DisplayName("회원 가입 처리 - 입력값 정상")
    @Test
    void signUpFormSubmit_with_correct_input() throws Exception {
        mockMvc.perform(post("/sign-up")
            .param("nickname", "jayden")
            .param("email", "email@email.com")
            .param("password", "12345678")
            .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/"));

        assertTrue(accountRepository.existsByEmail("email@email.com"));

        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }
}
