package br.com.kbmg.wshammeron.integration.controller;

import br.com.kbmg.wshammeron.config.recaptcha.v3.RecaptchaEnum;
import br.com.kbmg.wshammeron.enums.PermissionEnum;
import br.com.kbmg.wshammeron.integration.BaseEntityIntegrationTests;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.TOKEN_ACTIVATE_EXPIRED;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.USER_ACTIVATE_ACCOUNT;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.USER_OR_PASSWORD_INCORRECT;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.USER_PASSWORD_EXPIRED;
import static br.com.kbmg.wshammeron.integration.ResponseErrorExpect.thenReturnHttpError400_BadRequest;
import static constants.BaseTestsConstants.BEARER_TOKEN_TEST;
import static constants.BaseTestsConstants.USER_TEST_PASSWORD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityIT extends BaseEntityIntegrationTests {

    @BeforeEach
    public void beforeEach() {
        givenUserAuthenticatedWithoutRoles();
    }

    @AfterEach
    public void afterEach() {
        super.deleteUserAndAssociations(userAppLoggedTest);
    }

    @Test
    void loginAndGetToken_shouldReturnJwtToken() throws Exception {
        givenUserAuthenticatedWithPermission(PermissionEnum.PARTICIPANT);
        givenLoginDto();
        whenCallJwtServiceGenerateTokenShouldReturnValidJwt();
        whenRequestPostLoginAndGetToken();
        thenShouldReturnJwtToken();
        thenCheckIfRecaptchaServiceInvoked(RecaptchaEnum.LOGIN_ACTION);
    }

    @Test
    void loginAndGetToken_shouldReturnErrorIfIncorrectPassword() throws Exception {
        givenLoginDto(userAppLoggedTest.getEmail(), "incorrect password");
        whenRequestPostLoginAndGetToken();
        thenReturnHttpError400_BadRequest(perform, messagesService.get(USER_OR_PASSWORD_INCORRECT));
        thenCheckIfRecaptchaServiceInvoked(RecaptchaEnum.LOGIN_ACTION);
    }

    @Test
    void loginAndGetToken_shouldReturnErrorIfPasswordExpired() throws Exception {
        userAppLoggedTest.setPasswordExpireDate(LocalDateTime.now().minusDays(1));
        givenLoginDto();
        whenRequestPostLoginAndGetToken();
        thenReturnHttpError400_BadRequest(perform, messagesService.get(USER_PASSWORD_EXPIRED));
        thenCheckIfRecaptchaServiceInvoked(RecaptchaEnum.LOGIN_ACTION);
    }

    @Test
    void loginAndGetToken_shouldReturnErrorIfIncorrectEmail() throws Exception {
        givenLoginDto("email-not-exists@test.com", USER_TEST_PASSWORD);
        whenRequestPostLoginAndGetToken();
        thenReturnHttpError400_BadRequest(perform, messagesService.get(USER_OR_PASSWORD_INCORRECT));
        thenCheckIfRecaptchaServiceInvoked(RecaptchaEnum.LOGIN_ACTION);
    }

    @Test
    void loginAndGetToken_shouldReturnErrorIfAccountNotEnabled() throws Exception {
        givenUserLoggedNotEnabled();
        givenLoginDto(userAppLoggedTest.getEmail(), USER_TEST_PASSWORD);
        whenRequestPostLoginAndGetToken();
        thenReturnHttpError400_BadRequest(perform, messagesService.get(USER_ACTIVATE_ACCOUNT));
    }

    @Test
    void passwordRecovery_shouldReturnNoBody() throws Exception {
        givenActivateUserAccountRefreshDto(userAppLoggedTest.getEmail());
        givenMimeMessage();
        whenRequestPasswordRecovery();
        thenShouldReturnNoBody();
    }

    @Test
    void passwordRecoveryChange_shouldReturnNoBody() throws Exception {
        givenUserAuthenticatedWithPermission(PermissionEnum.PARTICIPANT);
        givenActivateUserAccountRefreshDto(userAppLoggedTest.getEmail());
        givenMimeMessage();
        givenUserChangePasswordDto();
        whenRequestPasswordRecoveryChange();
        thenShouldReturnNoBody();
    }

    @Test
    void registerUserAccount_shouldReturnNoBody() throws Exception {
        super.checkIfEmailAlreadyExistAndDeleteIfPresent();
        givenHeadersRequired();
        givenUserDto();
        givenMimeMessage();
        whenRequestRegisterUserAccount();
        thenShouldReturnNoBody();
    }

    @Test
    void registerUserPassword_shouldReturnNoBody() throws Exception {
        givenRegisterPasswordDto(userAppLoggedTest.getEmail());
        whenRequestRegisterUserPassword();
        thenShouldReturnNoBody();
    }

    @Test
    void activateUserAccount_shouldReturnNoBodyIfUserEnableSuccessful() throws Exception {
        givenUserAuthenticatedWithPermission(PermissionEnum.PARTICIPANT);
        givenUserLoggedNotEnabled();
        givenVerificationToken();
        givenUserTokenHashDto(userAppLoggedTest.getEmail());
        whenRequestActivateUserAccount();
        thenShouldReturnNoBody();
    }

    @Test
    void activateUserAccount_shouldReturnErrorIfTokenExpired() throws Exception {
        givenUserLoggedNotEnabled();
        givenVerificationTokenExpired();
        givenUserTokenHashDto(userAppLoggedTest.getEmail());
        whenRequestActivateUserAccount();
        thenReturnHttpError400_BadRequest(perform, messagesService.get(TOKEN_ACTIVATE_EXPIRED));
    }

    @Test
    void activateUserAccount_shouldReturnNoBodyIfUserAlreadyEnable() throws Exception {
        givenUserTokenHashDto(userAppLoggedTest.getEmail());
        whenRequestActivateUserAccount();
        thenShouldReturnNoBody();
    }

    @Test
    void resendMailToken_shouldReturnNoBody() throws Exception {
        givenUserLoggedNotEnabled();
        givenActivateUserAccountRefreshDto(userAppLoggedTest.getEmail());
        givenMimeMessage();
        whenRequestResendMailToken();
        thenShouldReturnNoBody();
    }

    @Test
    void resendMailToken_shouldReturnErrorIfFailedSendEmail() throws Exception {
        givenUserLoggedNotEnabled();
        givenActivateUserAccountRefreshDto(userAppLoggedTest.getEmail());
        givenMimeMessage();
        whenRequestResendMailToken();
        thenShouldReturnNoBody();
    }

    private void whenCallJwtServiceGenerateTokenShouldReturnValidJwt() {
        when(jwtServiceMockBean.generateToken(any(), any(), any())).thenReturn(BEARER_TOKEN_TEST);
    }

    private void whenRequestPostLoginAndGetToken() throws Exception {
        String endpoint = "/security/token-login";
        super.whenRequestPost(String.format(templateUrlRecaptcha, endpoint), loginDtoTest);
    }

    private void whenRequestPasswordRecovery() throws Exception {
        super.whenRequestPost("/security/password-recovery", activateUserAccountRefreshDtoTest);
    }

    private void whenRequestPasswordRecoveryChange() throws Exception {
        super.whenRequestPost("/security/password-recovery/change", userChangePasswordDtoTest);
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
