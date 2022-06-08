package br.com.kbmg.wshammeron.unit.service;

import br.com.kbmg.wshammeron.dto.user.UserWithPermissionDto;
import br.com.kbmg.wshammeron.dto.user.UserWithSinglePermissionDto;
import br.com.kbmg.wshammeron.enums.PermissionEnum;
import br.com.kbmg.wshammeron.exception.ForbiddenException;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.SpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.repository.UserPermissionRepository;
import br.com.kbmg.wshammeron.service.impl.UserPermissionServiceImpl;
import br.com.kbmg.wshammeron.unit.BaseUnitTests;
import br.com.kbmg.wshammeron.unit.annotation.WithSpaceOwnerTest;
import builder.UserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.USER_DOES_NOT_PERMISSION_TO_ACTION;
import static br.com.kbmg.wshammeron.unit.ExceptionAssertions.thenShouldThrowException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserPermissionServiceTest extends BaseUnitTests {

    @InjectMocks
    private UserPermissionServiceImpl userPermissionService;

    @Mock
    private UserPermissionRepository userPermissionRepositoryMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithSpaceOwnerTest
    void addPermissionToUser_shouldAdd() {
        UserApp userApp = givenUserAppFull(PermissionEnum.SPACE_OWNER);
        SpaceUserAppAssociation spaceUserAppAssociation = givenSpaceUserAppAssociation(userApp);

        when(userAppServiceMock.findUserLogged()).thenReturn(userApp);

        userPermissionService.addPermissionToUser(spaceUserAppAssociation, PermissionEnum.PARTICIPANT);

        assertAll(() -> verify(userPermissionRepositoryMock).save(any()),
                () -> verify(userAppServiceMock).findUserLogged()
        );
    }

    @Test
    void addPermissionToUser_shouldReturnErrorIfUserIsParticipant() {
        UserApp userApp = givenUserAppFull(PermissionEnum.PARTICIPANT);
        SpaceUserAppAssociation spaceUserAppAssociation = givenSpaceUserAppAssociation(userApp);

        when(userAppServiceMock.findUserLogged()).thenReturn(userApp);

        assertAll(
                () -> thenShouldThrowException(ForbiddenException.class,
                        spaceUserAppAssociation,
                        PermissionEnum.SPACE_OWNER,
                        userPermissionService::addPermissionToUser),
                () -> verify(userPermissionRepositoryMock, times(0)).save(any()),
                () -> verify(userAppServiceMock).findUserLogged(),
                () -> verify(messagesServiceMock).get(USER_DOES_NOT_PERMISSION_TO_ACTION)
        );
    }

    @Test
    void findAllBySpaceAndUserApp_shouldReturnPermissionsOfUserBySpace() {
        UserApp userApp = givenUserAppFull();
        Space space = givenSpace(userApp);

        when(userPermissionRepositoryMock.findAllByUserAppAndSpace(userApp, space))
                .thenReturn(List.of(PermissionEnum.PARTICIPANT));

        List<String> result = userPermissionService.findAllBySpaceAndUserApp(space, userApp);

        assertAll(() -> verify(userPermissionRepositoryMock).findAllByUserAppAndSpace(userApp, space),
                () -> assertEquals(List.of(PermissionEnum.PARTICIPANT.name()), result)
        );
    }

    @Test
    void checkPermissionsOfUsers_shouldReturnPermissionsOfUserBySpace() {
        UserApp userApp = givenUserAppFull();
        Space space = givenSpace(userApp);
        UserWithPermissionDto userWithPermissionDto = UserBuilder.generateUserWithPermissionDto(userApp, PermissionEnum.PARTICIPANT);
        UserWithSinglePermissionDto userWithSinglePermissionDto = UserBuilder.generateUserWithPermissionDto(userWithPermissionDto);
        Set<String> emailList = Set.of(userApp.getEmail());

        when(userPermissionRepositoryMock.findBySpaceAndEmailList(space, emailList))
                .thenReturn(List.of(userWithSinglePermissionDto));

        userPermissionService.checkPermissionsOfUsers(space, Set.of(userWithPermissionDto));

        assertAll(() -> verify(userPermissionRepositoryMock).findBySpaceAndEmailList(space, emailList));
    }

}
