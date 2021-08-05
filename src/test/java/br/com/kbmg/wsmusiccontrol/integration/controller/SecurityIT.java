package br.com.kbmg.wsmusiccontrol.integration.controller;

import br.com.kbmg.wsmusiccontrol.dto.ActivateUserAccountRefreshDto;
import br.com.kbmg.wsmusiccontrol.dto.LoginDto;
import br.com.kbmg.wsmusiccontrol.dto.UserDto;
import br.com.kbmg.wsmusiccontrol.integration.BaseIntegrationTests;
import builder.UserBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.lang.Nullable;
import org.springframework.test.web.servlet.ResultActions;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.TOKEN_ACTIVATE_FAILED_SEND;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.USER_ACTIVATE_ACCOUNT;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.USER_OR_PASSWORD_INCORRECT;
import static br.com.kbmg.wsmusiccontrol.integration.ResponseErrorExpect.thenReturnHttpError400_BadRequest;
import static constants.BaseTestsConstants.AUTHENTICATED_USER_TEST_EMAIL;
import static constants.BaseTestsConstants.AUTHENTICATED_USER_TEST_PASSWORD;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityIT extends BaseIntegrationTests {

    private LoginDto loginDtoTest;
    private UserDto userDtoTest;
    private ActivateUserAccountRefreshDto activateUserAccountRefreshDtoTest;

    @Nullable
    private Session session;

    @Test
    public void validateLoginAndGetToken_shouldReturnJwtToken() throws Exception {
        super.beforeAllTestsBase();
        givenLoginDto();
        whenRequestPostLoginAndGetToken();
        thenShouldReturnJwtToken();
    }

    @Test
    public void validateLoginAndGetToken_shouldReturnErrorIfIncorrectPassword() throws Exception {
        super.beforeAllTestsBase();
        givenLoginDto(AUTHENTICATED_USER_TEST_EMAIL, "incorrect password");
        whenRequestPostLoginAndGetToken();
        thenReturnHttpError401_Unauthorized(perform, messagesService.get(USER_OR_PASSWORD_INCORRECT));
    }

    private void thenReturnHttpError401_Unauthorized(ResultActions perform, String s) {
    }

    @Test
    public void validateLoginAndGetToken_shouldReturnErrorIfIncorrectEmail() throws Exception {
        super.beforeAllTestsBase();
        givenLoginDto("email-not-exists@test.com", AUTHENTICATED_USER_TEST_PASSWORD);
        whenRequestPostLoginAndGetToken();
        thenReturnHttpError401_Unauthorized(perform, messagesService.get(USER_OR_PASSWORD_INCORRECT));
    }

    @Test
    public void validateLoginAndGetToken_shouldReturnErrorIfAccountNotEnabled() throws Exception {
        super.beforeAllTestsBase();
        givenUserLoggedNotEnabled();
        givenLoginDto(AUTHENTICATED_USER_TEST_EMAIL, AUTHENTICATED_USER_TEST_PASSWORD);
        whenRequestPostLoginAndGetToken();
        thenReturnHttpError401_Unauthorized(perform, messagesService.get(USER_ACTIVATE_ACCOUNT));
    }

    @Test
    public void resendMailToken_shouldReturnNoBody() throws Exception {
        givenActivateUserAccountRefreshDto();
        whenRequestResendMailToken();
        thenShouldReturnNoBody();
    }

    @Test
    public void registerUserAccount_shouldReturnNoBody() throws Exception {
        givenHeadersRequired();
        givenUserDto();
        givenMimeMessage();
        whenRequestRegisterUserAccount();
        thenShouldReturnNoBody();
    }

    @Test
    public void registerUserAccount_shouldReturnErrorIfFailedSendTokenToEmail() throws Exception {
        givenHeadersRequired();
        givenUserDto();
        whenRequestRegisterUserAccount();
        thenReturnHttpError400_BadRequest(perform, messagesService.get(TOKEN_ACTIVATE_FAILED_SEND));
    }

    private void givenMimeMessage() {
        Properties javaMailProperties = new Properties();
        if (this.session == null) {
            this.session = Session.getInstance(javaMailProperties);
        }

        MimeMessage mimeMessageTest = new MimeMessage(session);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessageTest);
    }

    private void givenUserLoggedNotEnabled() {
        userAppLoggedTest.setEnabled(false);
        userAppRepository.save(userAppLoggedTest);
    }

    private void givenUserDto() {
        userDtoTest = UserBuilder.generateUserDto();
    }

    private void givenActivateUserAccountRefreshDto() {
        activateUserAccountRefreshDtoTest = UserBuilder.generateActivateUserAccountRefreshDto();
    }

    private void givenLoginDto() {
        loginDtoTest = UserBuilder.generateLoginDto(AUTHENTICATED_USER_TEST_EMAIL, AUTHENTICATED_USER_TEST_PASSWORD);
    }

    private void givenLoginDto(String email, String pass) {
        loginDtoTest = UserBuilder.generateLoginDto(email, pass);
    }

    private void whenRequestPostLoginAndGetToken() throws Exception {
        super.whenRequestPost("/security/token-login", loginDtoTest);
    }

    private void whenRequestResendMailToken() throws Exception {
        super.whenRequestPost("/security/token-activate/refresh", activateUserAccountRefreshDtoTest);
    }

    private void whenRequestRegisterUserAccount() throws Exception {
        super.whenRequestPost("/security/register", userDtoTest);
    }

    private void thenShouldReturnJwtToken() throws Exception {
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.jwtToken").isNotEmpty());
    }

    private void thenShouldReturnNoBody() throws Exception {
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

}
