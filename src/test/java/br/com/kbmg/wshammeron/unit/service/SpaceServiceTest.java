package br.com.kbmg.wshammeron.unit.service;

import br.com.kbmg.wshammeron.dto.space.SpaceRequestDto;
import br.com.kbmg.wshammeron.dto.space.overview.SpaceOverviewDto;
import br.com.kbmg.wshammeron.enums.SpaceStatusEnum;
import br.com.kbmg.wshammeron.event.producer.SpaceApproveProducer;
import br.com.kbmg.wshammeron.event.producer.SpaceRequestProducer;
import br.com.kbmg.wshammeron.exception.ForbiddenException;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.SpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.repository.SpaceRepository;
import br.com.kbmg.wshammeron.service.impl.SpaceServiceImpl;
import br.com.kbmg.wshammeron.unit.BaseUnitTests;
import builder.SpaceBuilder;
import builder.UserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.SPACE_ALREADY_EXIST;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.SPACE_APPROVE_NOT_FOUND_REQUESTED;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.SPACE_NOT_EXIST;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.SPACE_USER_NOT_EXIST;
import static br.com.kbmg.wshammeron.unit.ExceptionAssertions.thenShouldThrowException;
import static br.com.kbmg.wshammeron.unit.ExceptionAssertions.thenShouldThrowServiceException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SpaceServiceTest extends BaseUnitTests {

    @InjectMocks
    private SpaceServiceImpl spaceService;

    @Mock
    private SpaceRequestProducer spaceRequestProducerMock;

    @Mock
    private SpaceApproveProducer spaceApproveProducerMock;

    @Mock
    private SpaceRepository spaceRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void requestNewSpaceForUser_shouldSendSpringEvent() {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        UserApp userApp = UserBuilder.generateUserAppLogged();
        Space space = SpaceBuilder.generateSpace(userApp);
        SpaceRequestDto spaceRequestDto = SpaceBuilder.generateSpaceRequestDto(space);
        String spaceName = spaceRequestDto.getName();

        when(spaceRepository.findByName(spaceName)).thenReturn(Optional.empty());
        when(userAppServiceMock.findUserLogged()).thenReturn(userApp);

        spaceService.requestNewSpaceForUser(spaceRequestDto, mockedRequest);

        assertAll(
                () -> verify(spaceRepository).findByName(spaceName),
                () -> verify(userAppServiceMock).findUserLogged(),
                () -> verify(spaceRepository).save(any()),
                () -> verify(spaceRequestProducerMock).publishEvent(any(),any())
        );
    }

    @Test
    void requestNewSpaceForUser_shouldErrorIfSpaceNameAlreadyExist() {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        Space space = givenSpace();
        SpaceRequestDto spaceRequestDto = SpaceBuilder.generateSpaceRequestDto(space);
        String spaceName = spaceRequestDto.getName();

        when(spaceRepository.findByName(spaceName)).thenReturn(Optional.of(space));
        when(messagesServiceMock.get(SPACE_ALREADY_EXIST)).thenReturn("%s");

        assertAll(
                () -> thenShouldThrowServiceException(
                        spaceRequestDto, mockedRequest, spaceService::requestNewSpaceForUser),
                () -> verify(messagesServiceMock).get(SPACE_ALREADY_EXIST),
                () -> verify(spaceRepository).findByName(spaceName),
                () -> verify(userAppServiceMock, times(0)).findUserLogged(),
                () -> verify(spaceRepository, times(0)).save(any()),
                () -> verify(spaceRequestProducerMock, times(0)).publishEvent(any(),any())
        );
    }

    @Test
    void findByIdValidated_shouldReturnSpace() {
        UserApp userApp = givenUserAppFull();
        Space space = givenSpace(userApp);
        String spaceId = space.getId();

        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));

        spaceService.findByIdValidated(spaceId);

        assertAll(
                () -> verify(spaceRepository).findById(spaceId),
                () -> verify(messagesServiceMock, times(0)).get(SPACE_NOT_EXIST)
        );
    }

    @Test
    void findByIdValidated_shouldErrorIfNotExist() {
        UserApp userApp = givenUserAppFull();
        Space space = givenSpace(userApp);
        String spaceId = space.getId();

        when(spaceRepository.findById(spaceId)).thenReturn(Optional.empty());

        assertAll(
                () -> thenShouldThrowServiceException(spaceId, spaceService::findByIdValidated),
                () -> verify(spaceRepository).findById(spaceId),
                () -> verify(messagesServiceMock).get(SPACE_NOT_EXIST)
        );
    }

    @Test
    void findByIdAndUserAppValidated_shouldReturnSpaceRelatedOfUser() {
        UserApp userApp = givenUserAppFull();
        Space space = givenSpace(userApp);
        String spaceId = space.getId();

        when(spaceRepository.findByIdAndSpaceStatusAndUserApp(spaceId, SpaceStatusEnum.APPROVED, userApp)).thenReturn(Optional.of(space));

        spaceService.findByIdAndUserAppValidated(spaceId, userApp);

        assertAll(
                () -> verify(spaceRepository, times(0)).findById(spaceId),
                () -> verify(spaceRepository).findByIdAndSpaceStatusAndUserApp(spaceId, SpaceStatusEnum.APPROVED, userApp),
                () -> verify(messagesServiceMock, times(0)).get(SPACE_USER_NOT_EXIST)
        );
    }

    @Test
    void findByIdAndUserAppValidated_shouldReturnSpaceToSysAdmin() {
        UserApp userApp = givenUserAppFull();
        userApp.setIsSysAdmin(true);
        Space space = givenSpace(userApp);
        String spaceId = space.getId();

        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));

        spaceService.findByIdAndUserAppValidated(spaceId, userApp);

        assertAll(
                () -> verify(spaceRepository).findById(spaceId),
                () -> verify(spaceRepository, times(0)).findByIdAndSpaceStatusAndUserApp(any(), any(), any()),
                () -> verify(messagesServiceMock, times(0)).get(SPACE_USER_NOT_EXIST)
        );
    }

    @Test
    void findByIdAndUserAppValidated_shouldReturnErrorIfUserNotHaveTheSpace() {
        UserApp userApp = givenUserAppFull();
        Space space = givenSpace(userApp);
        String spaceId = space.getId();

        when(spaceRepository.findByIdAndSpaceStatusAndUserApp(spaceId, SpaceStatusEnum.APPROVED, userApp))
                .thenReturn(Optional.empty());

        assertAll(
                () -> thenShouldThrowException(ForbiddenException.class, spaceId, userApp, spaceService::findByIdAndUserAppValidated),
                () -> verify(spaceRepository, times(0)).findById(spaceId),
                () -> verify(spaceRepository).findByIdAndSpaceStatusAndUserApp(spaceId, SpaceStatusEnum.APPROVED, userApp),
                () -> verify(messagesServiceMock).get(SPACE_USER_NOT_EXIST)
        );
    }

    @Test
    void approveNewSpaceForUser_shouldSendSpringEvent() {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        UserApp userApp = givenUserAppFull();
        Space space = givenSpace(userApp);
        String spaceId = space.getId();
        space.setApprovedBy(null);
        space.setSpaceStatus(SpaceStatusEnum.REQUESTED);

        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));
        when(userAppServiceMock.findUserLogged()).thenReturn(userApp);

        spaceService.approveNewSpaceForUser(spaceId, SpaceStatusEnum.APPROVED, mockedRequest);

        assertAll(
                () -> verify(spaceRepository).findById(spaceId),
                () -> verify(userAppServiceMock).findUserLogged(),
                () -> verify(spaceRepository).save(any()),
                () -> verify(spaceUserAppAssociationServiceMock).createAssociationToSpaceOwner(any(), any()),
                () -> verify(spaceApproveProducerMock).publishEvent(any(), any())
        );
    }

    @Test
    void approveNewSpaceForUser_shouldReturnIfSpaceAlreadyApproved() {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        UserApp userApp = givenUserAppFull();
        Space space = givenSpace(userApp);
        String spaceId = space.getId();

        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));

        spaceService.approveNewSpaceForUser(spaceId, SpaceStatusEnum.APPROVED, mockedRequest);

        assertAll(
                () -> verify(spaceRepository).findById(spaceId),
                () -> verify(userAppServiceMock, times(0)).findUserLogged(),
                () -> verify(spaceRepository, times(0)).save(any()),
                () -> verify(spaceUserAppAssociationServiceMock, times(0)).createAssociationToSpaceOwner(any(), any()),
                () -> verify(spaceApproveProducerMock, times(0)).publishEvent(any(), any())
        );
    }

    @Test
    void approveNewSpaceForUser_shouldReturnErrorIfSpaceRequestNotDefined() {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        UserApp userApp = givenUserAppFull();
        Space space = givenSpace(userApp);
        String spaceId = space.getId();
        space.setRequestedBy(null);
        space.setApprovedBy(null);
        space.setSpaceStatus(SpaceStatusEnum.REQUESTED);

        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));

        assertAll(
                () -> thenShouldThrowServiceException(
                        () -> spaceService.approveNewSpaceForUser(spaceId, SpaceStatusEnum.APPROVED, mockedRequest)
                ),
                () -> verify(messagesServiceMock).get(SPACE_APPROVE_NOT_FOUND_REQUESTED),
                () -> verify(spaceRepository).findById(spaceId),
                () -> verify(userAppServiceMock, times(0)).findUserLogged(),
                () -> verify(spaceRepository, times(0)).save(any()),
                () -> verify(spaceUserAppAssociationServiceMock, times(0))
                        .createAssociationToSpaceOwner(any(), any()),
                () -> verify(spaceApproveProducerMock, times(0)).publishEvent(any(), any())
        );
    }

    @Test
    void findAllSpaceByStatus_shouldReturnSpaceList() {
        Space space = givenSpace();
        SpaceStatusEnum spaceStatus = space.getSpaceStatus();
        List<Space> expectedList = List.of(space);

        when(spaceRepository.findAllBySpaceStatus(spaceStatus)).thenReturn(expectedList);

        List<Space> result = spaceService.findAllSpaceByStatus(spaceStatus);

        assertAll(
                () -> verify(spaceRepository).findAllBySpaceStatus(spaceStatus),
                () -> assertEquals(expectedList, result)
        );
    }

    @Test
    void findAllSpacesByUserApp_shouldReturnSpaceListToParticipant() {
        UserApp userApp = givenUserAppFull();
        Space space = givenSpace(userApp);
        List<Space> expectedList = List.of(space);

        when(userAppServiceMock.findUserLogged()).thenReturn(userApp);

        List<Space> result = spaceService.findAllSpacesByUserApp();

        assertAll(
                () -> verify(userAppServiceMock).findUserLogged(),
                () -> verify(spaceRepository, times(0)).findAll(any(Sort.class)),
                () -> assertEquals(expectedList, result)
        );
    }

    @Test
    void findAllSpacesByUserApp_shouldReturnSpaceListToSysAdmin() {
        UserApp userApp = givenUserAppFull();
        userApp.setIsSysAdmin(true);
        Space space = givenSpace(userApp);
        List<Space> expectedList = List.of(space);

        when(userAppServiceMock.findUserLogged()).thenReturn(userApp);
        when(spaceRepository.findAll(any(Sort.class))).thenReturn(expectedList);

        List<Space> result = spaceService.findAllSpacesByUserApp();

        assertAll(
                () -> verify(userAppServiceMock).findUserLogged(),
                () -> verify(spaceRepository).findAll(any(Sort.class)),
                () -> assertEquals(expectedList, result)
        );
    }

    @Test
    void changeViewSpaceUser_shouldReturnNewJwt() {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        UserApp userApp = givenUserAppFull();
        Space space = givenSpace(userApp);
        String spaceId = space.getId();
        String validJwt = givenValidJwt(userApp);

        when(userAppServiceMock.findUserLogged()).thenReturn(userApp);
        when(spaceRepository.findByIdAndSpaceStatusAndUserApp(spaceId, SpaceStatusEnum.APPROVED, userApp))
                .thenReturn(Optional.of(space));
        when(jwtServiceMock.updateSpaceOnToken(mockedRequest, space)).thenReturn(validJwt);

        String result = spaceService.changeViewSpaceUser(spaceId, mockedRequest);

        assertAll(
                () -> verify(userAppServiceMock).findUserLogged(),
                () -> verify(spaceRepository, times(0)).findById(spaceId),
                () -> verify(spaceRepository)
                        .findByIdAndSpaceStatusAndUserApp(spaceId, SpaceStatusEnum.APPROVED, userApp),
                () -> verify(messagesServiceMock, times(0)).get(SPACE_USER_NOT_EXIST),
                () -> verify(spaceUserAppAssociationServiceMock).updateLastAccessedSpace(userApp, space),
                () -> verify(jwtServiceMock).updateSpaceOnToken(mockedRequest, space),
                () -> assertEquals(validJwt, result)
        );
    }

    @Test
    void findLastAccessedSpace_shouldReturnSpaceToParticipant() {
        UserApp userApp = givenUserAppFull();
        SpaceUserAppAssociation spaceUserAppAssociation = userApp
                .getSpaceUserAppAssociationList()
                .stream()
                .findFirst()
                .orElseThrow();
        Space space = spaceUserAppAssociation.getSpace();

        when(userAppServiceMock.findUserLogged()).thenReturn(userApp);
        when(spaceUserAppAssociationServiceMock.findLastAccessedSpace(userApp)).thenReturn(spaceUserAppAssociation);

        Space result = spaceService.findLastAccessedSpace();

        assertAll(
                () -> verify(userAppServiceMock).findUserLogged(),
                () -> verify(spaceUserAppAssociationServiceMock).findLastAccessedSpace(userApp),
                () -> assertEquals(space, result)
        );
    }

    @Test
    void findSpaceOverview_shouldReturnSpaceOverviewDto() {
        UserApp userApp = givenUserAppFull();
        Space space = givenSpace(userApp);
        SpaceOverviewDto spaceOverviewDto = SpaceBuilder.generateSpaceOverviewDto(space, userApp);

        when(spaceRepository.findById(any())).thenReturn(Optional.of(space));
        when(userAppServiceMock.findByEmail(any())).thenReturn(Optional.of(userApp));
        when(spaceUserAppAssociationServiceMock.findUserOverviewBySpace(space)).thenReturn(spaceOverviewDto.getUserList());
        when(musicServiceMock.findMusicOverview(space)).thenReturn(spaceOverviewDto.getMusicList());
        when(eventServiceMock.findEventOverviewBySpace(space)).thenReturn(spaceOverviewDto.getEventList());

        SpaceOverviewDto result = spaceService.findSpaceOverview();

        assertAll(
                () -> verify(spaceRepository).findById(any()),
                () -> verify(userAppServiceMock).findByEmail(any()),
                () -> verify(spaceUserAppAssociationServiceMock).findUserOverviewBySpace(space),
                () -> verify(musicServiceMock).findMusicOverview(space),
                () -> verify(eventServiceMock).findEventOverviewBySpace(space),
                () -> assertEquals(spaceOverviewDto, result)
        );
    }

}
