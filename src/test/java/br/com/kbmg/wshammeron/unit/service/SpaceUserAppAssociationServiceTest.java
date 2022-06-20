package br.com.kbmg.wshammeron.unit.service;

import br.com.kbmg.wshammeron.enums.PermissionEnum;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.SpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.repository.SpaceUserAppAssociationRepository;
import br.com.kbmg.wshammeron.service.impl.SpaceUserAppAssociationServiceImpl;
import br.com.kbmg.wshammeron.unit.BaseUnitTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.EVENT_USER_LIST_INVALID;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.USER_HAS_PERMISSION;
import static br.com.kbmg.wshammeron.unit.ExceptionAssertions.thenShouldThrowServiceException;
import static constants.BaseTestsConstants.generateRandomEmail;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SpaceUserAppAssociationServiceTest extends BaseUnitTests {

    @InjectMocks
    private SpaceUserAppAssociationServiceImpl spaceUserAppAssociationService;

    @Mock
    private SpaceUserAppAssociationRepository spaceUserAppAssociationRepositoryMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAssociationToParticipant_shouldCreateAssociation() {
        UserApp userApp = givenUserAppFull(PermissionEnum.SPACE_OWNER);
        SpaceUserAppAssociation spaceUserAppAssociation = givenSpaceUserAppAssociation(userApp);
        Space space = spaceUserAppAssociation.getSpace();

        when(spaceUserAppAssociationRepositoryMock.findBySpaceAndUserApp(space, userApp))
                .thenReturn(Optional.of(spaceUserAppAssociation));

        spaceUserAppAssociationService.createAssociationToParticipant(space, userApp);

        assertAll(
                () -> verify(spaceUserAppAssociationRepositoryMock).findBySpaceAndUserApp(space, userApp),
                () -> verify(spaceUserAppAssociationRepositoryMock).save(spaceUserAppAssociation),
                () -> verify(userPermissionServiceMock).addPermissionToUser(any(), any())
        );
    }

    @Test
    void createAssociationToParticipant_shouldReturnErrorIfUserHasPermission() {
        UserApp userApp = givenUserAppFull(PermissionEnum.PARTICIPANT);
        SpaceUserAppAssociation spaceUserAppAssociation = givenSpaceUserAppAssociation(userApp);
        Space space = spaceUserAppAssociation.getSpace();

        when(spaceUserAppAssociationRepositoryMock.findBySpaceAndUserApp(space, userApp))
                .thenReturn(Optional.of(spaceUserAppAssociation));

        assertAll(
                () -> thenShouldThrowServiceException(
                        space, userApp, spaceUserAppAssociationService::createAssociationToParticipant),
                () -> verify(spaceUserAppAssociationRepositoryMock).findBySpaceAndUserApp(space, userApp),
                () -> verify(spaceUserAppAssociationRepositoryMock, times(0)).save(spaceUserAppAssociation),
                () -> verify(userPermissionServiceMock, times(0)).addPermissionToUser(any(), any()),
                () -> verify(messagesServiceMock).get(USER_HAS_PERMISSION)
        );
    }

    @Test
    void createAssociationToSpaceOwner_shouldCreateAssociation() {
        UserApp userApp = givenUserAppFull();
        SpaceUserAppAssociation spaceUserAppAssociation = givenSpaceUserAppAssociation(userApp);
        Space space = spaceUserAppAssociation.getSpace();

        when(spaceUserAppAssociationRepositoryMock.findBySpaceAndUserApp(space, userApp))
                .thenReturn(Optional.of(spaceUserAppAssociation));

        spaceUserAppAssociationService.createAssociationToSpaceOwner(space, userApp);

        assertAll(
                () -> verify(spaceUserAppAssociationRepositoryMock).findBySpaceAndUserApp(space, userApp),
                () -> verify(spaceUserAppAssociationRepositoryMock).save(spaceUserAppAssociation),
                () -> verify(userPermissionServiceMock).addPermissionToUser(any(), any())
        );
    }

    @Test
    void updateLastAccessedSpace_shouldUpdate() {
        UserApp userApp = givenUserAppFull();
        SpaceUserAppAssociation spaceUserAppAssociation = givenSpaceUserAppAssociation(userApp);
        Space space = spaceUserAppAssociation.getSpace();

        when(spaceUserAppAssociationRepositoryMock.findByUserAppAndLastAccessedSpaceTrue(userApp))
                .thenReturn(spaceUserAppAssociation);

        spaceUserAppAssociationService.updateLastAccessedSpace(userApp, space);

        assertAll(
                () -> verify(spaceUserAppAssociationRepositoryMock).findByUserAppAndLastAccessedSpaceTrue(userApp),
                () -> verify(spaceUserAppAssociationRepositoryMock, times(2)).save(spaceUserAppAssociation)
        );
    }

    @Test
    void findAllBySpaceAndEmailList_shouldReturnList() {
        UserApp userApp = givenUserAppFull();
        SpaceUserAppAssociation spaceUserAppAssociation = givenSpaceUserAppAssociation(userApp);
        Space space = spaceUserAppAssociation.getSpace();
        Set<String> emailList = Set.of(userApp.getEmail());
        Set<SpaceUserAppAssociation> associationList = Set.of(spaceUserAppAssociation);

        when(spaceUserAppAssociationRepositoryMock.findBySpaceAndEmailUserList(space, emailList))
                .thenReturn(associationList);

        Set<SpaceUserAppAssociation> result = spaceUserAppAssociationService.findAllBySpaceAndEmailList(space, emailList);

        assertAll(
                () -> assertEquals(associationList, result),
                () -> verify(spaceUserAppAssociationRepositoryMock).findBySpaceAndEmailUserList(space, emailList)
        );
    }

    @Test
    void findAllBySpaceAndEmailList_shouldReturnErrorIfEmailListNotExistsOnSpace() {
        UserApp userApp = givenUserAppFull();
        SpaceUserAppAssociation spaceUserAppAssociation = givenSpaceUserAppAssociation(userApp);
        Space space = spaceUserAppAssociation.getSpace();
        Set<String> emailList = Set.of(generateRandomEmail());

        when(spaceUserAppAssociationRepositoryMock.findBySpaceAndEmailUserList(space, emailList))
                .thenReturn(Collections.emptySet());

        assertAll(
                () -> thenShouldThrowServiceException(
                        space, emailList, spaceUserAppAssociationService::findAllBySpaceAndEmailList),
                () -> verify(spaceUserAppAssociationRepositoryMock)
                        .findBySpaceAndEmailUserList(space, emailList),
                () -> verify(messagesServiceMock).get(EVENT_USER_LIST_INVALID)
        );
    }

}
