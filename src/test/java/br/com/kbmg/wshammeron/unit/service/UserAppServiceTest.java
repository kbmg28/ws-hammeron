package br.com.kbmg.wshammeron.unit.service;

import br.com.kbmg.wshammeron.dto.user.RegisterDto;
import br.com.kbmg.wshammeron.dto.user.RegisterPasswordDto;
import br.com.kbmg.wshammeron.dto.user.UserDto;
import br.com.kbmg.wshammeron.dto.user.UserWithPermissionDto;
import br.com.kbmg.wshammeron.enums.PermissionEnum;
import br.com.kbmg.wshammeron.exception.ServiceException;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.repository.UserAppRepository;
import br.com.kbmg.wshammeron.service.impl.UserAppServiceImpl;
import br.com.kbmg.wshammeron.unit.BaseUnitTests;
import builder.SpaceBuilder;
import constants.BaseTestsConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.USER_ALREADY_EXISTS;
import static br.com.kbmg.wshammeron.unit.ExceptionAssertions.thenShouldThrowServiceException;
import static builder.UserBuilder.generateUserWithPermissionDto;
import static constants.BaseTestsConstants.USER_TEST_CELLPHONE;
import static constants.BaseTestsConstants.USER_TEST_NAME;
import static constants.BaseTestsConstants.USER_TEST_PASSWORD;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAppServiceTest extends BaseUnitTests {

    @InjectMocks
    private UserAppServiceImpl userAppService;

    @Mock
    private UserAppRepository repositoryMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerNewUserAccount_shouldCreateUser() {
        String email = BaseTestsConstants.generateRandomEmail();
        RegisterDto registerDto = givenRegisterDto(email);
        UserApp userApp = givenNewUserAppDto(email, false);

        when(repositoryMock.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());
        when(repositoryMock.save(any())).thenReturn(userApp);

        UserApp result = userAppService.registerNewUserAccount(registerDto);

        assertAll(() -> verify(repositoryMock).findByEmailIgnoreCase(email),
                () -> verify(repositoryMock).save(any()),
                () -> assertEquals(userApp, result));
    }

    @Test
    void registerNewUserAccount_shouldReturnExceptionIfAlreadyExists() {
        String email = BaseTestsConstants.generateRandomEmail();
        RegisterDto registerDto = givenRegisterDto(email);
        UserApp userApp = givenNewUserAppDto(email, true);

        when(repositoryMock.findByEmailIgnoreCase(email)).thenReturn(Optional.of(userApp));

        thenShouldThrowServiceException(ServiceException.class, registerDto, userAppService::registerNewUserAccount, null);
        assertAll(() -> verify(repositoryMock).findByEmailIgnoreCase(email),
                () -> verify(messagesServiceMock).get(USER_ALREADY_EXISTS));
    }

    @Test
    void registerUserPassword_shouldSavePasswordOfUser() {
        String email = BaseTestsConstants.generateRandomEmail();
        RegisterPasswordDto registerPasswordDto = givenRegisterPasswordDto(email);
        UserApp userApp = givenNewUserAppDto(email, true);

        when(repositoryMock.findByEmailIgnoreCase(email)).thenReturn(Optional.of(userApp));

        userAppService.registerUserPassword(registerPasswordDto);

        assertAll(() -> verify(repositoryMock).findByEmailIgnoreCase(email),
                () -> verify(repositoryMock).save(any()));
    }

    @Test
    void findAllBySpace_shouldReturnUserList() {
        String email = BaseTestsConstants.generateRandomEmail();
        UserApp userApp = givenNewUserAppDto(email, true);
        Space space = SpaceBuilder.generateSpace(userApp);
        UserWithPermissionDto userWithPermissionDto = generateUserWithPermissionDto(userApp, PermissionEnum.PARTICIPANT);

        when(spaceServiceMock.findByIdValidated(any())).thenReturn(space);
        when(repositoryMock.findAllBySpace(space)).thenReturn(List.of(userApp));
        when(userAppMapperMock.toUserWithPermissionDtoList(List.of(userApp))).thenReturn(Set.of(userWithPermissionDto));

        Set<UserWithPermissionDto> result = userAppService.findAllBySpace(space.getId());

        assertAll(() -> verify(spaceServiceMock).findByIdValidated(any()),
                () -> verify(repositoryMock).findAllBySpace(space),
                () -> verify(userAppMapperMock).toUserWithPermissionDtoList(any()),
                () -> verify(userPermissionServiceMock).checkPermissionsOfUsers(any(), any()),
                () -> assertEquals(Set.of(userWithPermissionDto), result));
    }

    @Test
    void addPermissionToUserInSpace_shouldAddPermissionSpaceOwner() {
        String email = BaseTestsConstants.generateRandomEmail();
        UserApp userApp = givenNewUserAppDto(email, true);
        Space space = SpaceBuilder.generateSpace(userApp);

        when(repositoryMock.findByEmailIgnoreCase(email)).thenReturn(Optional.of(userApp));
        when(spaceServiceMock.findByIdValidated(any())).thenReturn(space);

        userAppService.addPermissionToUserInSpace(email, space.getId(), PermissionEnum.SPACE_OWNER);

        assertAll(() -> verify(repositoryMock).findByEmailIgnoreCase(email),
                () -> verify(spaceServiceMock).findByIdValidated(any()),
                () -> verify(repositoryMock, times(0)).save(any()),
                () -> verify(spaceUserAppAssociationServiceMock).createAssociationToSpaceOwner(space, userApp));
    }

    @Test
    void addPermissionToUserInSpace_shouldAddPermissionParticipantToUserNotPreviousExists() {
        String email = BaseTestsConstants.generateRandomEmail();
        UserApp userApp = givenNewUserAppDto(email, true);
        Space space = SpaceBuilder.generateSpace(userApp);

        when(spaceServiceMock.findByIdValidated(any())).thenReturn(space);
        when(repositoryMock.save(any())).thenReturn(userApp);

        userAppService.addPermissionToUserInSpace(email, space.getId(), PermissionEnum.PARTICIPANT);

        assertAll(() -> verify(repositoryMock).findByEmailIgnoreCase(email),
                () -> verify(spaceServiceMock).findByIdValidated(any()),
                () -> verify(repositoryMock).save(any()),
                () -> verify(spaceUserAppAssociationServiceMock).createAssociationToParticipant(space, userApp));
    }

    @Test
    void updateUserLogged_shouldReturnUserUpdated() {
        String email = BaseTestsConstants.generateRandomEmail();
        UserApp userApp = givenNewUserAppDto(email, true);
        UserDto userDto = givenUserDto(email);

        when(repositoryMock.findByEmailIgnoreCase(any())).thenReturn(Optional.of(userApp));
        when(repositoryMock.save(any())).thenReturn(userApp);

        UserApp result = userAppService.updateUserLogged(userDto);

        assertAll(() -> verify(repositoryMock).findByEmailIgnoreCase(any()),
                () -> verify(repositoryMock).save(any()),
                () -> assertEquals(userApp, result));
    }

    @Test
    void deleteCascade_shouldDeleteAnUserAndAllAssociations() {
        UserApp userApp = givenUserAppFull();

        when(repositoryMock.findByEmailIgnoreCase(any())).thenReturn(Optional.of(userApp));

        userAppService.deleteCascade(userApp.getEmail());

        assertAll(() -> verify(repositoryMock).findByEmailIgnoreCase(any()),
                () -> verify(verificationTokenServiceMock).deleteByUserApp(userApp),
                () -> verify(eventSpaceUserAppAssociationServiceMock).deleteInBatch(any()),
                () -> verify(userPermissionServiceMock).deleteInBatch(any()),
                () -> verify(spaceUserAppAssociationServiceMock).deleteInBatch(any()));
    }

    private UserDto givenUserDto(String email) {
        return new UserDto(USER_TEST_NAME, email, USER_TEST_CELLPHONE);
    }

    private RegisterDto givenRegisterDto(String email) {
        return new RegisterDto(USER_TEST_NAME, email, USER_TEST_CELLPHONE);
    }

    private UserApp givenNewUserAppDto(String email, Boolean isEnabled) {
        UserApp userApp = new UserApp();

        userApp.setEmail(email);
        userApp.setEnabled(isEnabled);
        userApp.setIsSysAdmin(false);
        userApp.setName(USER_TEST_NAME);
        userApp.setCellPhone(USER_TEST_CELLPHONE);

        return userApp;
    }

    private RegisterPasswordDto givenRegisterPasswordDto(String email) {
        return new RegisterPasswordDto(email, USER_TEST_PASSWORD);
    }
}
