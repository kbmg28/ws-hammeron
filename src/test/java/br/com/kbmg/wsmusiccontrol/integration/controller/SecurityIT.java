package br.com.kbmg.wsmusiccontrol.integration.controller;

import br.com.kbmg.wsmusiccontrol.config.recaptcha.v3.RecaptchaEnum;
import br.com.kbmg.wsmusiccontrol.integration.BaseEntityIntegrationTests;
import org.junit.jupiter.api.Test;

import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.TOKEN_ACTIVATE_EXPIRED;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.TOKEN_ACTIVATE_FAILED_SEND;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.USER_ACTIVATE_ACCOUNT;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.USER_OR_PASSWORD_INCORRECT;
import static br.com.kbmg.wsmusiccontrol.integration.ResponseErrorExpect.thenReturnHttpError400_BadRequest;
import static br.com.kbmg.wsmusiccontrol.integration.ResponseErrorExpect.thenReturnHttpError401_Unauthorized;
import static constants.BaseTestsConstants.AUTHENTICATED_USER_TEST_EMAIL;
import static constants.BaseTestsConstants.AUTHENTICATED_USER_TEST_PASSWORD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityIT extends BaseEntityIntegrationTests {

    @Test
    public void loginAndGetToken_shouldReturnJwtToken() throws Exception {
        super.beforeAllTestsBase();
        givenLoginDto();
        whenRequestPostLoginAndGetToken();
        thenShouldReturnJwtToken();
        thenCheckIfRecaptchaServiceInvoked(RecaptchaEnum.LOGIN_ACTION);
    }

    @Test
    public void loginAndGetToken_shouldReturnErrorIfIncorrectPassword() throws Exception {
        super.beforeAllTestsBase();
        givenLoginDto(AUTHENTICATED_USER_TEST_EMAIL, "incorrect password");
        whenRequestPostLoginAndGetToken();
        thenReturnHttpError401_Unauthorized(perform, messagesService.get(USER_OR_PASSWORD_INCORRECT));
        thenCheckIfRecaptchaServiceInvoked(RecaptchaEnum.LOGIN_ACTION);
    }

    @Test
    public void loginAndGetToken_shouldReturnErrorIfIncorrectEmail() throws Exception {
        super.beforeAllTestsBase();
        givenLoginDto("email-not-exists@test.com", AUTHENTICATED_USER_TEST_PASSWORD);
        whenRequestPostLoginAndGetToken();
        thenReturnHttpError401_Unauthorized(perform, messagesService.get(USER_OR_PASSWORD_INCORRECT));
        thenCheckIfRecaptchaServiceInvoked(RecaptchaEnum.LOGIN_ACTION);
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
    public void passwordRecovery_shouldReturnNoBody() throws Exception {
        super.beforeAllTestsBase();
        givenActivateUserAccountRefreshDto();
        givenMimeMessage();
        whenRequestPasswordRecovery();
        thenShouldReturnNoBody();
    }

    @Test
    public void passwordRecovery_shouldReturnErrorIfFailedSendEmail() throws Exception {
        super.beforeAllTestsBase();
        givenActivateUserAccountRefreshDto();
        whenRequestPasswordRecovery();
        thenReturnHttpError400_BadRequest(perform,
                messagesService.get("user.email.password.recovery.failed.send"));

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
    public void registerUserAccount_shouldReturnErrorIfFailedSendEmail() throws Exception {
        givenHeadersRequired();
        givenUserDto();
        whenRequestRegisterUserAccount();
        thenReturnHttpError400_BadRequest(perform, messagesService.get(TOKEN_ACTIVATE_FAILED_SEND));
    }

    @Test
    public void registerUserPassword_shouldReturnNoBody() throws Exception {
        super.beforeAllTestsBase();
        givenRegisterPasswordDto();
        whenRequestRegisterUserPassword();
        thenShouldReturnNoBody();
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
    public void activateUserAccount_shouldReturnErrorIfTokenExpired() throws Exception {
        super.beforeAllTestsBase();
        givenUserLoggedNotEnabled();
        givenVerificationTokenExpired();
        givenUserTokenHashDto();
        whenRequestActivateUserAccount();
        thenReturnHttpError400_BadRequest(perform, messagesService.get(TOKEN_ACTIVATE_EXPIRED));
    }

    @Test
    public void activateUserAccount_shouldReturnNoBodyIfUserAlreadyEnable() throws Exception {
        super.beforeAllTestsBase();
        givenUserTokenHashDto();
        whenRequestActivateUserAccount();
        thenShouldReturnNoBody();
    }

    @Test
    public void resendMailToken_shouldReturnNoBody() throws Exception {
        super.beforeAllTestsBase();
        givenUserLoggedNotEnabled();
        givenActivateUserAccountRefreshDto();
        givenMimeMessage();
        whenRequestResendMailToken();
        thenShouldReturnNoBody();
    }

    @Test
    public void resendMailToken_shouldReturnErrorIfFailedSendEmail() throws Exception {
        super.beforeAllTestsBase();
        givenUserLoggedNotEnabled();
        givenActivateUserAccountRefreshDto();
        givenMimeMessage();
        whenRequestResendMailToken();
        thenShouldReturnNoBody();
    }

    private void whenRequestPostLoginAndGetToken() throws Exception {
        String endpoint = "/security/token-login";
        super.whenRequestPost(String.format(templateUrlRecaptcha, endpoint), loginDtoTest);
    }

    private void whenRequestPasswordRecovery() throws Exception {
        super.whenRequestPost("/security/password-recovery", activateUserAccountRefreshDtoTest);
    }

    private void whenRequestResendMailToken() throws Exception {
        super.whenRequestPost("/security/register/token/refresh", activateUserAccountRefreshDtoTest);
    }

    private void whenRequestRegisterUserAccount() throws Exception {
        super.whenRequestPost("/security/register", userDtoTest);
    }

    private void whenRequestRegisterUserPassword() throws Exception {
        super.whenRequestPost("/security/register/password", registerPasswordDtoTest);
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
