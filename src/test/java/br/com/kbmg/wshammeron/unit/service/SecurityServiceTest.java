package br.com.kbmg.wshammeron.unit.service;

import br.com.kbmg.wshammeron.dto.user.ActivateUserAccountRefreshDto;
import br.com.kbmg.wshammeron.dto.user.LoginDto;
import br.com.kbmg.wshammeron.dto.user.RegisterDto;
import br.com.kbmg.wshammeron.dto.user.UserChangePasswordDto;
import br.com.kbmg.wshammeron.dto.user.UserTokenHashDto;
import br.com.kbmg.wshammeron.event.producer.PasswordRecoveryProducer;
import br.com.kbmg.wshammeron.event.producer.RegistrationProducer;
import br.com.kbmg.wshammeron.exception.BadUserInfoException;
import br.com.kbmg.wshammeron.model.SpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.model.VerificationToken;
import br.com.kbmg.wshammeron.repository.VerificationTokenRepository;
import br.com.kbmg.wshammeron.service.impl.SecurityServiceImpl;
import br.com.kbmg.wshammeron.unit.BaseUnitTests;
import builder.UserBuilder;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;

import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.DATA_INVALID;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.TOKEN_ACTIVATE_EXPIRED;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.USER_ACTIVATE_ACCOUNT;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.USER_OR_PASSWORD_INCORRECT;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.USER_PASSWORD_EXPIRED;
import static br.com.kbmg.wshammeron.unit.ExceptionAssertions.thenShouldThrowException;
import static br.com.kbmg.wshammeron.unit.ExceptionAssertions.thenShouldThrowServiceException;
import static builder.UserBuilder.generateActivateUserAccountRefreshDto;
import static builder.UserBuilder.generateRegisterDto;
import static builder.UserBuilder.generateUserChangePasswordDto;
import static builder.UserBuilder.generateUserTokenHashDto;
import static builder.UserBuilder.generateVerificationToken;
import static constants.BaseTestsConstants.ANY_VALUE;
import static constants.BaseTestsConstants.SECRET_UNIT_TEST;
import static constants.BaseTestsConstants.BEARER_TOKEN_TEST;
import static constants.BaseTestsConstants.USER_TEST_PASSWORD;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SecurityServiceTest extends BaseUnitTests {

    @InjectMocks
    private SecurityServiceImpl securityService;

    @Mock
    private VerificationTokenRepository tokenRepositoryMock;

    @Mock
    private RegistrationProducer registrationProducerMock;

    @Mock
    private PasswordRecoveryProducer passwordRecoveryProducerMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateLoginAndGetToken_shouldReturnValidJwt() {
        UserApp userApp = givenUserAppFull();
        SpaceUserAppAssociation spaceUserAppAssociation = userApp.getSpaceUserAppAssociationList().stream().findFirst().orElseThrow();
        LoginDto loginDto = UserBuilder.generateLoginDto(userApp.getEmail(), USER_TEST_PASSWORD);
        String validJwt = givenValidJwt(userApp);

        when(userAppServiceMock.findByEmail(userApp.getEmail())).thenReturn(Optional.of(userApp));
        when(spaceUserAppAssociationServiceMock.findLastAccessedSpace(userApp)).thenReturn(spaceUserAppAssociation);
        when(jwtServiceMock.generateToken(any(), any(), any())).thenReturn(validJwt);

        String tokenResult = securityService.validateLoginAndGetToken(loginDto);


        assertAll(() -> verify(userAppServiceMock).findByEmail(userApp.getEmail()),
                () -> verify(spaceUserAppAssociationServiceMock).findLastAccessedSpace(userApp),
                () -> verify(jwtServiceMock).generateToken(any(), any(), any()),
                () -> verify(messagesServiceMock).get(any()),
                () -> assertDoesNotThrow(() -> Jwts.parser().setSigningKey(SECRET_UNIT_TEST).parseClaimsJws(tokenResult))
        );
    }

    @Test
    void validateLoginAndGetToken_shouldReturnErrorIfUserEmailNotExists() {
        UserApp userApp = givenUserAppFull();
        LoginDto loginDto = UserBuilder.generateLoginDto(userApp.getEmail(), USER_TEST_PASSWORD);

        assertAll(() -> thenShouldThrowException(BadUserInfoException.class, loginDto, securityService::validateLoginAndGetToken),
                () -> verify(userAppServiceMock).findByEmail(userApp.getEmail()),
                () -> verify(messagesServiceMock).get(USER_OR_PASSWORD_INCORRECT));
    }

    @Test
    void validateLoginAndGetToken_shouldReturnErrorIfUserNotEnabled() {
        UserApp userApp = givenUserAppFull();
        LoginDto loginDto = UserBuilder.generateLoginDto(userApp.getEmail(), USER_TEST_PASSWORD);
        userApp.setEnabled(false);

        when(userAppServiceMock.findByEmail(userApp.getEmail())).thenReturn(Optional.of(userApp));

        assertAll(() -> thenShouldThrowException(BadUserInfoException.class, loginDto, securityService::validateLoginAndGetToken),
                () -> verify(userAppServiceMock).findByEmail(userApp.getEmail()),
                () -> verify(messagesServiceMock).get(USER_ACTIVATE_ACCOUNT));
    }

    @Test
    void validateLoginAndGetToken_shouldReturnErrorIfUserPasswordExpired() {
        UserApp userApp = givenUserAppFull();
        LoginDto loginDto = UserBuilder.generateLoginDto(userApp.getEmail(), USER_TEST_PASSWORD);
        userApp.setPasswordExpireDate(LocalDateTime.now().minusDays(1));

        when(userAppServiceMock.findByEmail(userApp.getEmail())).thenReturn(Optional.of(userApp));

        assertAll(() -> thenShouldThrowException(BadUserInfoException.class, loginDto, securityService::validateLoginAndGetToken),
                () -> verify(userAppServiceMock).findByEmail(userApp.getEmail()),
                () -> verify(messagesServiceMock).get(USER_PASSWORD_EXPIRED));
    }

    @Test
    void validateLoginAndGetToken_shouldReturnErrorIfUserPasswordIncorrect() {
        UserApp userApp = givenUserAppFull();
        LoginDto loginDto = UserBuilder.generateLoginDto(userApp.getEmail(), ANY_VALUE);

        when(userAppServiceMock.findByEmail(userApp.getEmail())).thenReturn(Optional.of(userApp));

        assertAll(() -> thenShouldThrowException(BadUserInfoException.class, loginDto, securityService::validateLoginAndGetToken),
                () -> verify(userAppServiceMock).findByEmail(userApp.getEmail()),
                () -> verify(messagesServiceMock).get(USER_OR_PASSWORD_INCORRECT));
    }

    @Test
    void registerNewUserAccount_shouldRegisterAndSendSpringEvent() {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        UserApp userApp = givenUserAppFull();
        RegisterDto registerDto = generateRegisterDto(userApp.getEmail());

        when(userAppServiceMock.registerNewUserAccount(registerDto)).thenReturn(userApp);

        securityService.registerNewUserAccount(registerDto, mockedRequest);


        assertAll(() -> verify(userAppServiceMock).registerNewUserAccount(registerDto),
                () -> verify(registrationProducerMock).publishEvent(mockedRequest, userApp)
        );
    }

    @Test
    void activateUserAccount_shouldActivate() {
        UserApp userApp = givenUserAppFull();
        userApp.setEnabled(false);
        String userEmail = userApp.getEmail();
        UserTokenHashDto userTokenHashDto = generateUserTokenHashDto(userEmail);
        VerificationToken verificationToken = generateVerificationToken(userApp);

        when(userAppServiceMock.findByEmail(userEmail)).thenReturn(Optional.of(userApp));
        when(tokenRepositoryMock.findByTokenAndUserApp(BEARER_TOKEN_TEST, userApp)).thenReturn(verificationToken);

        securityService.activateUserAccount(userTokenHashDto);


        assertAll(() -> verify(messagesServiceMock).get(TOKEN_ACTIVATE_EXPIRED),
                () -> verify(userAppServiceMock).findByEmail(userEmail),
                () -> verify(tokenRepositoryMock).findByTokenAndUserApp(BEARER_TOKEN_TEST, userApp),
                () -> verify(userAppServiceMock).saveUserEnabled(userApp),
                () -> verify(spaceUserAppAssociationServiceMock).updateLastAccessedSpace(any(), any())
        );
    }

    @Test
    void activateUserAccount_shouldReturnErrorIfUserEmailNotExists() {
        UserApp userApp = givenUserAppFull();
        String userEmail = userApp.getEmail();
        UserTokenHashDto userTokenHashDto = generateUserTokenHashDto(userEmail);

        when(userAppServiceMock.findByEmail(userEmail)).thenReturn(Optional.empty());

        assertAll(() -> thenShouldThrowServiceException(userTokenHashDto, securityService::activateUserAccount),
                () -> verify(userAppServiceMock).findByEmail(userApp.getEmail()),
                () -> verify(messagesServiceMock).get(TOKEN_ACTIVATE_EXPIRED));
    }

    @Test
    void activateUserAccount_shouldTokenExpired() {
        UserApp userApp = givenUserAppFull();
        userApp.setEnabled(false);
        String userEmail = userApp.getEmail();
        UserTokenHashDto userTokenHashDto = generateUserTokenHashDto(userEmail);

        when(userAppServiceMock.findByEmail(userEmail)).thenReturn(Optional.of(userApp));

        assertAll(() -> thenShouldThrowServiceException(userTokenHashDto, securityService::activateUserAccount),
                () -> verify(userAppServiceMock).findByEmail(userApp.getEmail()),
                () -> verify(messagesServiceMock).get(TOKEN_ACTIVATE_EXPIRED),
                () -> verify(tokenRepositoryMock).findByTokenAndUserApp(any(), any())
        );
    }

    @Test
    void createVerificationToken_shouldCreate() {
        UserApp userApp = givenUserAppFull();

        securityService.createVerificationToken(userApp, BEARER_TOKEN_TEST);


        assertAll(() -> verify(tokenRepositoryMock).findByUserApp(userApp),
                () -> verify(tokenRepositoryMock).save(any()),
                () -> verify(tokenRepositoryMock, times(0)).delete(any())
        );
    }

    @Test
    void createVerificationToken_shouldCreateDeletingPreviousToken() {
        UserApp userApp = givenUserAppFull();
        VerificationToken verificationToken = generateVerificationToken(userApp);

        when(tokenRepositoryMock.findByUserApp(userApp)).thenReturn(Optional.of(verificationToken));

        securityService.createVerificationToken(userApp, BEARER_TOKEN_TEST);

        assertAll(() -> verify(tokenRepositoryMock).findByUserApp(userApp),
                () -> verify(tokenRepositoryMock).save(any()),
                () -> verify(tokenRepositoryMock).delete(any())
        );
    }

    @Test
    void resendMailToken_shouldSendSpringEvent() {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        UserApp userApp = givenUserAppFull();
        userApp.setEnabled(false);
        String userEmail = userApp.getEmail();
        ActivateUserAccountRefreshDto activateUserAccountRefreshDto =
                generateActivateUserAccountRefreshDto(userEmail);

        when(userAppServiceMock.findByEmail(userEmail)).thenReturn(Optional.of(userApp));

        securityService.resendMailToken(activateUserAccountRefreshDto, mockedRequest);

        assertAll(
                () -> verify(userAppServiceMock).findByEmail(userEmail),
                () -> verify(tokenRepositoryMock).findByUserApp(userApp),
                () -> verify(tokenRepositoryMock, times(0)).delete(any()),
                () -> verify(registrationProducerMock).publishEvent(mockedRequest, userApp)
        );
    }

    @Test
    void resendMailToken_shouldReturnIfUserAlreadyActivated() {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        UserApp userApp = givenUserAppFull();
        String userEmail = userApp.getEmail();
        ActivateUserAccountRefreshDto activateUserAccountRefreshDto =
                generateActivateUserAccountRefreshDto(userEmail);

        when(userAppServiceMock.findByEmail(userEmail)).thenReturn(Optional.of(userApp));

        securityService.resendMailToken(activateUserAccountRefreshDto, mockedRequest);

        assertAll(
                () -> verify(userAppServiceMock).findByEmail(userEmail),
                () -> verify(tokenRepositoryMock, times(0)).findByUserApp(userApp),
                () -> verify(tokenRepositoryMock, times(0)).delete(any()),
                () -> verify(registrationProducerMock, times(0)).publishEvent(mockedRequest, userApp)
        );
    }

    @Test
    void passwordRecovery_shouldSendSpringEvent() {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        UserApp userApp = givenUserAppFull();
        String userEmail = userApp.getEmail();
        ActivateUserAccountRefreshDto activateUserAccountRefreshDto =
                generateActivateUserAccountRefreshDto(userEmail);

        when(userAppServiceMock.findByEmail(userEmail)).thenReturn(Optional.of(userApp));

        securityService.passwordRecovery(activateUserAccountRefreshDto, mockedRequest);

        assertAll(
                () -> verify(userAppServiceMock).findByEmail(userEmail),
                () -> verify(passwordRecoveryProducerMock).publishEvent(mockedRequest, userApp)
        );
    }

    @Test
    void passwordRecoveryChange_shouldChangePassword() {
        UserApp userApp = givenUserAppFull();
        String userEmail = userApp.getEmail();
        UserChangePasswordDto userChangePasswordDto = generateUserChangePasswordDto(userApp);

        when(userAppServiceMock.findByEmail(userEmail)).thenReturn(Optional.of(userApp));

        securityService.passwordRecoveryChange(userChangePasswordDto);

        assertAll(
                () -> verify(userAppServiceMock).findByEmail(userEmail),
                () -> verify(userAppServiceMock).encodePasswordAndSave(any(), any(), any())
        );
    }

    @Test
    void passwordRecoveryChange_shouldReturnErrorIfUserEmailNotExists() {
        UserApp userApp = givenUserAppFull();
        String userEmail = userApp.getEmail();
        UserChangePasswordDto userChangePasswordDto = generateUserChangePasswordDto(userApp);

        when(userAppServiceMock.findByEmail(userEmail)).thenReturn(Optional.empty());

        assertAll(
                () -> thenShouldThrowException(
                        BadUserInfoException.class, userChangePasswordDto, securityService::passwordRecoveryChange),
                () -> verify(messagesServiceMock).get(DATA_INVALID),
                () -> verify(userAppServiceMock).findByEmail(userEmail),
                () -> verify(userAppServiceMock, times(0)).encodePasswordAndSave(any(), any(), any())
        );
    }

    @Test
    void passwordRecoveryChange_shouldReturnErrorIfUserPasswordIncorrect() {
        UserApp userApp = givenUserAppFull();
        String userEmail = userApp.getEmail();
        UserChangePasswordDto userChangePasswordDto = generateUserChangePasswordDto(userApp);
        userChangePasswordDto.setTemporaryPassword("incorrect");

        when(userAppServiceMock.findByEmail(userEmail)).thenReturn(Optional.of(userApp));

        assertAll(
                () -> thenShouldThrowException(
                        BadUserInfoException.class, userChangePasswordDto, securityService::passwordRecoveryChange),
                () -> verify(messagesServiceMock).get(DATA_INVALID),
                () -> verify(userAppServiceMock).findByEmail(userEmail),
                () -> verify(userAppServiceMock, times(0)).encodePasswordAndSave(any(), any(), any())
        );
    }

}
