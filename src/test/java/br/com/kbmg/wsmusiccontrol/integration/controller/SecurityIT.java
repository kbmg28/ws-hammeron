package br.com.kbmg.wsmusiccontrol.integration.controller;

import br.com.kbmg.wsmusiccontrol.dto.user.ActivateUserAccountRefreshDto;
import br.com.kbmg.wsmusiccontrol.dto.user.LoginDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserTokenHashDto;
import br.com.kbmg.wsmusiccontrol.integration.BaseIntegrationTests;
import br.com.kbmg.wsmusiccontrol.model.VerificationToken;
import br.com.kbmg.wsmusiccontrol.repository.VerificationTokenRepository;
import builder.UserBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.TOKEN_ACTIVATE_FAILED_SEND;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.USER_ACTIVATE_ACCOUNT;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.USER_OR_PASSWORD_INCORRECT;
import static br.com.kbmg.wsmusiccontrol.integration.ResponseErrorExpect.thenReturnHttpError400_BadRequest;
import static br.com.kbmg.wsmusiccontrol.integration.ResponseErrorExpect.thenReturnHttpError401_Unauthorized;
import static constants.BaseTestsConstants.AUTHENTICATED_USER_TEST_EMAIL;
import static constants.BaseTestsConstants.AUTHENTICATED_USER_TEST_PASSWORD;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityIT extends BaseIntegrationTests {

    private LoginDto loginDtoTest;
    private UserDto userDtoTest;
    private UserTokenHashDto userTokenHashDtoTest;
    private ActivateUserAccountRefreshDto activateUserAccountRefreshDtoTest;

    @Autowired
    protected VerificationTokenRepository verificationTokenRepository;

    @Nullable
    private Session session;

    @Test
    public void loginAndGetToken_shouldReturnJwtToken() throws Exception {
        super.beforeAllTestsBase();
        givenLoginDto();
        whenRequestPostLoginAndGetToken();
        thenShouldReturnJwtToken();
    }

    @Test
    public void loginAndGetToken_shouldReturnErrorIfIncorrectPassword() throws Exception {
        super.beforeAllTestsBase();
        givenLoginDto(AUTHENTICATED_USER_TEST_EMAIL, "incorrect password");
        whenRequestPostLoginAndGetToken();
        thenReturnHttpError401_Unauthorized(perform, messagesService.get(USER_OR_PASSWORD_INCORRECT));
    }

    @Test
    public void loginAndGetToken_shouldReturnErrorIfIncorrectEmail() throws Exception {
        super.beforeAllTestsBase();
        givenLoginDto("email-not-exists@test.com", AUTHENTICATED_USER_TEST_PASSWORD);
        whenRequestPostLoginAndGetToken();
        thenReturnHttpError401_Unauthorized(perform, messagesService.get(USER_OR_PASSWORD_INCORRECT));
    }

    @Test
    public void loginAndGetToken_shouldReturnErrorIfAccountNotEnabled() throws Exception {
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

    @Test
    public void activateUserAccount_shouldReturnNoBodyIfUserEnableSuccessful() throws Exception {
        super.beforeAllTestsBase();
        givenUserLoggedNotEnabled();
        givenVerificationToken();
        givenUserTokenHashDto();
        whenRequestActivateUserAccount();
        thenShouldReturnNoBody();
    }

    @Test
    public void activateUserAccount_shouldReturnNoBodyIfUserAlreadyEnable() throws Exception {
        super.beforeAllTestsBase();
        givenUserTokenHashDto();
        whenRequestActivateUserAccount();
        thenShouldReturnNoBody();
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

    private void givenUserTokenHashDto() {
        userTokenHashDtoTest = UserBuilder.generateUserTokenHashDto();
    }

    private void givenLoginDto() {
        loginDtoTest = UserBuilder.generateLoginDto(AUTHENTICATED_USER_TEST_EMAIL, AUTHENTICATED_USER_TEST_PASSWORD);
    }

    private void givenLoginDto(String email, String pass) {
        loginDtoTest = UserBuilder.generateLoginDto(email, pass);
    }

    private void givenVerificationToken() {
        VerificationToken verificationTokenTest = UserBuilder.generateVerificationToken(userAppLoggedTest);
        verificationTokenRepository.save(verificationTokenTest);
    }

    private void whenRequestPostLoginAndGetToken() throws Exception {
        super.whenRequestPost("/security/token-login", loginDtoTest);
    }

    private void whenRequestResendMailToken() throws Exception {
        super.whenRequestPost("/security/register/token/refresh", activateUserAccountRefreshDtoTest);
    }

    private void whenRequestRegisterUserAccount() throws Exception {
        super.whenRequestPost("/security/register", userDtoTest);
    }

    private void whenRequestActivateUserAccount() throws Exception {
        super.whenRequestPost("/security/register/token", userTokenHashDtoTest);
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
