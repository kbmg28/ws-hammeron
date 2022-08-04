package br.com.kbmg.wshammeron.unit.service;

import br.com.kbmg.wshammeron.dto.event.EventDetailsDto;
import br.com.kbmg.wshammeron.dto.event.EventDto;
import br.com.kbmg.wshammeron.dto.event.EventWithMusicListDto;
import br.com.kbmg.wshammeron.enums.RangeDateFilterEnum;
import br.com.kbmg.wshammeron.model.Event;
import br.com.kbmg.wshammeron.model.EventMusicAssociation;
import br.com.kbmg.wshammeron.model.Music;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.repository.EventRepository;
import br.com.kbmg.wshammeron.repository.projection.EventWithTotalAssociationsProjection;
import br.com.kbmg.wshammeron.service.impl.EventServiceImpl;
import br.com.kbmg.wshammeron.unit.BaseUnitTests;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.EVENT_ALREADY_EXIST;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.EVENT_CREATE_DATETIME_INVALID;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.EVENT_DATE_RANGE_REQUIRED;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.EVENT_DO_NOT_EXIST;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.EVENT_DO_NOT_EXIST_ON_SPACE;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.EVENT_IS_NOT_EDITABLE;
import static br.com.kbmg.wshammeron.unit.ExceptionAssertions.thenShouldThrowServiceException;
import static builder.EventBuilder.generateEventDetailsDto;
import static builder.EventBuilder.generateEventDto;
import static builder.EventBuilder.generateEventWithMusicListDto;
import static builder.EventBuilder.generateEventWithTotalAssociationsProjection;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventServiceTest extends BaseUnitTests {

    @InjectMocks
    private EventServiceImpl eventService;

    @Mock
    private EventRepository eventRepository;

    @BeforeAll
    static void setupClass() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllEventsBySpace_shouldReturnEventDtoListToNextEvents() {
        UserApp userApp = super.givenUserAppFull();
        Space space = super.givenSpace(userApp);
        Event event = givenNextEvent(userApp, space);
        EventWithTotalAssociationsProjection projection = generateEventWithTotalAssociationsProjection(event);
        EventDto eventDto = generateEventDto(event);
        String spaceId = space.getId();

        when(userAppServiceMock.findUserLogged()).thenReturn(userApp);
        when(spaceServiceMock.findByIdValidated(spaceId)).thenReturn(space);
        when(eventRepository.findAllBySpaceAndDateTimeEventGreaterThanEqual(any(), any(), any(), any()))
                .thenReturn(List.of(projection));

        List<EventDto> result = eventService.findAllEventsBySpace(spaceId, null, true, null);

        assertAll(
                () -> verify(userAppServiceMock).findUserLogged(),
                () -> verify(spaceServiceMock).findByIdValidated(spaceId),
                () -> verify(eventRepository).findAllBySpaceAndDateTimeEventGreaterThanEqual(any(), any(), any(), any()),
                () -> verify(eventRepository, times(0))
                        .findAllBySpaceAndDateTimeEventRange(any(), any(), any(), any()),
                () -> assertEquals(List.of(eventDto), result)
        );
    }

    @Test
    void findAllEventsBySpace_shouldReturnEventDtoListToOldEvents() {
        UserApp userApp = super.givenUserAppFull();
        Space space = super.givenSpace(userApp);
        Event event = givenOldEvent(userApp, space);
        EventWithTotalAssociationsProjection projection = generateEventWithTotalAssociationsProjection(event);
        EventDto eventDto = generateEventDto(event);
        String spaceId = space.getId();

        when(userAppServiceMock.findUserLogged()).thenReturn(userApp);
        when(spaceServiceMock.findByIdValidated(spaceId)).thenReturn(space);
        when(eventRepository.findAllBySpaceAndDateTimeEventRange(any(), any(), any(), any()))
                .thenReturn(List.of(projection));

        List<EventDto> result = eventService.findAllEventsBySpace(spaceId, null, false, RangeDateFilterEnum.LAST_THIRTY_DAYS);

        assertAll(
                () -> verify(userAppServiceMock).findUserLogged(),
                () -> verify(spaceServiceMock).findByIdValidated(spaceId),
                () -> verify(eventRepository, times(0))
                        .findAllBySpaceAndDateTimeEventGreaterThanEqual(any(), any(), any(), any()),
                () -> verify(eventRepository)
                        .findAllBySpaceAndDateTimeEventRange(any(), any(), any(), any()),
                () -> assertEquals(List.of(eventDto), result)
        );
    }

    @Test
    void findAllEventsBySpace_shouldReturnErrorIfRangeNull() {
        UserApp userApp = super.givenUserAppFull();
        Space space = super.givenSpace(userApp);
        String spaceId = space.getId();

        when(userAppServiceMock.findUserLogged()).thenReturn(userApp);
        when(spaceServiceMock.findByIdValidated(spaceId)).thenReturn(space);

        assertAll(() -> thenShouldThrowServiceException(
                        () -> eventService.findAllEventsBySpace(spaceId, null, false, null)),
                () -> verify(userAppServiceMock).findUserLogged(),
                () -> verify(spaceServiceMock).findByIdValidated(spaceId),
                () -> verify(messagesServiceMock).get(EVENT_DATE_RANGE_REQUIRED));
    }

    @Test
    void findByIdValidated_shouldReturnEventDetailsDto() {
        UserApp userApp = super.givenUserAppFull();
        Space space = super.givenSpace(userApp);
        Event event = givenNextEvent(userApp, space);
        EventDetailsDto eventDetailsDto = generateEventDetailsDto(event, userApp);
        String eventId = event.getId();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventSpaceUserAppAssociationServiceMock.findAllUserAppByEvent(event)).thenReturn(List.of(userApp));
        when(userAppMapperMock.toUserDtoList(any())).thenReturn(eventDetailsDto.getUserList());
        when(eventMusicAssociationServiceMock.findAllMusicByEvent(event)).thenReturn(new ArrayList<>(event.getEventMusicList()));
        when(musicMapperMock.toMusicFullWithOrderDtoList(any())).thenReturn(eventDetailsDto.getMusicList());

        EventDetailsDto result = eventService.findByIdValidated(eventId);

        assertAll(
                () -> verify(eventRepository).findById(eventId),
                () -> verify(eventSpaceUserAppAssociationServiceMock).findAllUserAppByEvent(event),
                () -> verify(userAppMapperMock).toUserDtoList(any()),
                () -> verify(eventMusicAssociationServiceMock).findAllMusicByEvent(event),
                () -> verify(musicMapperMock).toMusicFullWithOrderDtoList(any()),
                () -> assertEquals(eventDetailsDto, result)
        );
    }

    @Test
    void findByIdValidated_shouldReturnErrorIfEventDoNotExist() {
        assertAll(() -> thenShouldThrowServiceException(UUID.randomUUID().toString(), eventService::findByIdValidated),
                () -> verify(messagesServiceMock).get(EVENT_DO_NOT_EXIST));
    }

    @Test
    void createEvent_shouldReturnEventDto() {
        UserApp userApp = super.givenUserAppFull();
        Space space = super.givenSpace(userApp);
        Event event = givenNextEvent(userApp, space);
        EventWithMusicListDto eventWithMusicListDto = generateEventWithMusicListDto(event, userApp);
        String spaceId = space.getId();
        EventDto eventDto = generateEventDto(event);

        when(spaceServiceMock.findByIdValidated(spaceId)).thenReturn(space);
        when(eventMusicAssociationServiceMock.createAssociation(any(), any()))
                .thenReturn(event.getEventMusicList());
        when(eventSpaceUserAppAssociationServiceMock.createAssociation(any(), any(), any(), any()))
                .thenReturn(event.getSpaceUserAppAssociationList());

        EventDto result = eventService.createEvent(spaceId, eventWithMusicListDto);
        result.setId(event.getId());
        result.setIsUserLoggedIncluded(true);

        assertAll(
                () -> verify(spaceServiceMock).findByIdValidated(spaceId),
                () -> verify(eventRepository).findBySpaceAndDateTimeEvent(any(), any()),
                () -> verify(eventRepository).save(any()),
                () -> verify(eventMusicAssociationServiceMock).createAssociation(any(), any()),
                () -> verify(eventSpaceUserAppAssociationServiceMock)
                        .createAssociation(any(), any(), any(), any()),
                () -> assertEquals(eventDto, result)
        );
    }

    @Test
    void createEvent_shouldReturnErrorIfDateEventIsBeforeNow() {
        UserApp userApp = super.givenUserAppFull();
        Space space = super.givenSpace(userApp);
        Event event = givenOldEvent(userApp, space);
        EventWithMusicListDto eventWithMusicListDto = generateEventWithMusicListDto(event, userApp);
        String spaceId = space.getId();

        assertAll(() -> thenShouldThrowServiceException(spaceId, eventWithMusicListDto, eventService::createEvent),
                () -> verify(messagesServiceMock).get(EVENT_CREATE_DATETIME_INVALID));
    }

    @Test
    void createEvent_shouldReturnErrorIfDateTimeEventAlreadyExist() {
        UserApp userApp = super.givenUserAppFull();
        Space space = super.givenSpace(userApp);
        Event event = givenNextEvent(userApp, space);
        EventWithMusicListDto eventWithMusicListDto = generateEventWithMusicListDto(event, userApp);
        String spaceId = space.getId();

        when(spaceServiceMock.findByIdValidated(spaceId)).thenReturn(space);
        when(eventRepository.findBySpaceAndDateTimeEvent(any(), any())).thenReturn(Optional.of(event));

        assertAll(
                () -> thenShouldThrowServiceException(spaceId, eventWithMusicListDto, eventService::createEvent),
                () -> verify(messagesServiceMock).get(EVENT_ALREADY_EXIST),
                () -> verify(spaceServiceMock).findByIdValidated(spaceId),
                () -> verify(eventRepository).findBySpaceAndDateTimeEvent(any(), any())
        );
    }

    @Test
    void editEvent_shouldReturnEventDto() {
        UserApp userApp = super.givenUserAppFull();
        Space space = super.givenSpace(userApp);
        Event event = givenNextEvent(userApp, space);
        EventWithMusicListDto eventWithMusicListDto = generateEventWithMusicListDto(event, userApp);
        String spaceId = space.getId();
        String eventId = event.getId();
        EventDto eventDto = generateEventDto(event);

        when(spaceServiceMock.findByIdValidated(spaceId)).thenReturn(space);
        when(eventRepository.findBySpaceAndId(space, eventId)).thenReturn(Optional.of(event));
        when(eventMusicAssociationServiceMock.updateAssociations(any(), any()))
                .thenReturn(event.getEventMusicList());
        when(eventSpaceUserAppAssociationServiceMock.updateAssociations(any(), any(), any()))
                .thenReturn(event.getSpaceUserAppAssociationList());

        EventDto result = eventService.editEvent(spaceId, eventId, eventWithMusicListDto);
        result.setIsUserLoggedIncluded(true);

        assertAll(
                () -> verify(spaceServiceMock).findByIdValidated(spaceId),
                () -> verify(eventRepository).findBySpaceAndId(space, eventId),
                () -> verify(eventMusicAssociationServiceMock).updateAssociations(any(), any()),
                () -> verify(eventSpaceUserAppAssociationServiceMock)
                        .updateAssociations(any(), any(), any()),
                () -> assertEquals(eventDto, result)
        );
    }

    @Test
    void editEvent_shouldReturnErrorIfEventDoNotExistOnSpace() {
        UserApp userApp = super.givenUserAppFull();
        Space space = super.givenSpace(userApp);
        Event event = givenNextEvent(userApp, space);
        EventWithMusicListDto eventWithMusicListDto = generateEventWithMusicListDto(event, userApp);
        String spaceId = space.getId();
        String eventId = event.getId();

        when(spaceServiceMock.findByIdValidated(spaceId)).thenReturn(space);
        when(eventRepository.findBySpaceAndId(space, eventId)).thenReturn(Optional.empty());

        assertAll(
                () -> thenShouldThrowServiceException(
                        () -> eventService.editEvent(spaceId, eventId, eventWithMusicListDto)),
                () -> verify(messagesServiceMock).get(EVENT_DO_NOT_EXIST_ON_SPACE),
                () -> verify(spaceServiceMock).findByIdValidated(spaceId),
                () -> verify(eventRepository).findBySpaceAndId(space, eventId)
        );
    }

    @Test
    void editEvent_shouldReturnErrorIfDateEventIsBeforeNow() {
        UserApp userApp = super.givenUserAppFull();
        Space space = super.givenSpace(userApp);
        Event event = givenOldEvent(userApp, space);
        EventWithMusicListDto eventWithMusicListDto = generateEventWithMusicListDto(event, userApp);
        String spaceId = space.getId();
        String eventId = event.getId();

        when(spaceServiceMock.findByIdValidated(spaceId)).thenReturn(space);
        when(eventRepository.findBySpaceAndId(space, eventId)).thenReturn(Optional.of(event));

        assertAll(
                () -> thenShouldThrowServiceException(
                        () -> eventService.editEvent(spaceId, eventId, eventWithMusicListDto)),
                () -> verify(messagesServiceMock).get(EVENT_IS_NOT_EDITABLE),
                () -> verify(spaceServiceMock).findByIdValidated(spaceId),
                () -> verify(eventRepository).findBySpaceAndId(space, eventId)
        );
    }

    @Test
    void deleteEvent_shouldDelete() {
        UserApp userApp = super.givenUserAppFull();
        Space space = super.givenSpace(userApp);
        Event event = givenNextEvent(userApp, space);
        String spaceId = space.getId();
        String eventId = event.getId();

        when(spaceServiceMock.findByIdValidated(spaceId)).thenReturn(space);
        when(eventRepository.findBySpaceAndId(space, eventId)).thenReturn(Optional.of(event));

        eventService.deleteEvent(spaceId, eventId);

        assertAll(
                () -> verify(spaceServiceMock).findByIdValidated(spaceId),
                () -> verify(eventRepository).findBySpaceAndId(space, eventId),
                () -> verify(eventMusicAssociationServiceMock).deleteInBatch(any()),
                () -> verify(eventSpaceUserAppAssociationServiceMock).deleteInBatch(any()),
                () -> verify(eventRepository).delete(any()),
                () -> verify(eventSpaceUserAppAssociationServiceMock)
                        .sendNotificationToAssociations(any(), any(), any())
        );
    }

    @Test
    void deleteEvent_shouldReturnErrorIfEventDoNotExistOnSpace() {
        UserApp userApp = super.givenUserAppFull();
        Space space = super.givenSpace(userApp);
        Event event = givenNextEvent(userApp, space);
        String spaceId = space.getId();
        String eventId = event.getId();

        when(spaceServiceMock.findByIdValidated(spaceId)).thenReturn(space);
        when(eventRepository.findBySpaceAndId(space, eventId)).thenReturn(Optional.empty());

        assertAll(
                () -> thenShouldThrowServiceException(spaceId, eventId, eventService::deleteEvent),
                () -> verify(messagesServiceMock).get(EVENT_DO_NOT_EXIST_ON_SPACE),
                () -> verify(spaceServiceMock).findByIdValidated(any())
        );
    }

    @Test
    void deleteEvent_shouldReturnErrorIfDateEventIsBeforeNow() {
        UserApp userApp = super.givenUserAppFull();
        Space space = super.givenSpace(userApp);
        Event event = givenOldEvent(userApp, space);
        String spaceId = space.getId();
        String eventId = event.getId();

        when(spaceServiceMock.findByIdValidated(spaceId)).thenReturn(space);
        when(eventRepository.findBySpaceAndId(space, eventId)).thenReturn(Optional.of(event));

        assertAll(
                () -> thenShouldThrowServiceException(spaceId, eventId, eventService::deleteEvent),
                () -> verify(messagesServiceMock).get(EVENT_IS_NOT_EDITABLE),
                () -> verify(spaceServiceMock).findByIdValidated(spaceId),
                () -> verify(eventRepository).findBySpaceAndId(space, eventId)
        );
    }

    @Test
    void findAllEventsByDateEvent_shouldReturnEventList() {
        UserApp userApp = super.givenUserAppFull();
        Space space = super.givenSpace(userApp);
        Event event = givenNextEvent(userApp, space);
        OffsetDateTime dateTimeEvent = event.getDateTimeEvent();
        List<Event> eventList = List.of(event);

        when(eventRepository.findAllEventsByDateTimeEvent(dateTimeEvent)).thenReturn(eventList);

        List<Event> result = eventService.findAllEventsByDateEvent(dateTimeEvent);

        assertAll(
                () -> verify(eventRepository).findAllEventsByDateTimeEvent(dateTimeEvent),
                () -> assertEquals(eventList, result)
        );
    }

    @Test
    void addOrRemoveMusicOnEvent_shouldReturn() {
        UserApp userApp = super.givenUserAppFull();
        Space space = super.givenSpace(userApp);
        Event event = givenNextEvent(userApp, space);
        Music music = event.getEventMusicList().stream().map(EventMusicAssociation::getMusic).findFirst().orElseThrow();
        String spaceId = space.getId();
        String musicId = music.getId();
        String eventId = event.getId();

        when(spaceServiceMock.findByIdValidated(spaceId)).thenReturn(space);
        when(eventRepository.findBySpaceAndId(space, eventId)).thenReturn(Optional.of(event));

        eventService.addOrRemoveMusicOnEvent(spaceId, eventId, musicId);

        assertAll(
                () -> verify(spaceServiceMock).findByIdValidated(spaceId),
                () -> verify(eventRepository).findBySpaceAndId(space, eventId),
                () -> verify(eventMusicAssociationServiceMock).addOrRemoveMusicOnEvent(musicId, space, event)
        );
    }

    @Test
    void addOrRemoveMusicOnEvent_shouldReturnErrorIfEventDontBelongToSpace() {
        UserApp userApp = super.givenUserAppFull();
        Space space = super.givenSpace(userApp);
        Event event = givenNextEvent(userApp, space);
        Music music = event.getEventMusicList().stream().map(EventMusicAssociation::getMusic).findFirst().orElseThrow();
        String spaceId = space.getId();
        String musicId = music.getId();
        String eventId = event.getId();

        when(spaceServiceMock.findByIdValidated(spaceId)).thenReturn(space);
        when(eventRepository.findBySpaceAndId(space, eventId)).thenReturn(Optional.empty());

        assertAll(
                () -> thenShouldThrowServiceException(
                        () -> eventService.addOrRemoveMusicOnEvent(spaceId, eventId, musicId)),
                () -> verify(messagesServiceMock).get(EVENT_DO_NOT_EXIST_ON_SPACE),
                () -> verify(spaceServiceMock).findByIdValidated(any())
        );
    }

    @Test
    void addOrRemoveMusicOnEvent_shouldReturnErrorIfDateEventIsBeforeNow() {
        UserApp userApp = super.givenUserAppFull();
        Space space = super.givenSpace(userApp);
        Event event = givenOldEvent(userApp, space);
        Music music = event.getEventMusicList().stream().map(EventMusicAssociation::getMusic).findFirst().orElseThrow();
        String spaceId = space.getId();
        String musicId = music.getId();
        String eventId = event.getId();

        when(spaceServiceMock.findByIdValidated(spaceId)).thenReturn(space);
        when(eventRepository.findBySpaceAndId(space, eventId)).thenReturn(Optional.of(event));

        assertAll(
                () -> thenShouldThrowServiceException(
                        () -> eventService.addOrRemoveMusicOnEvent(spaceId, eventId, musicId)),
                () -> verify(messagesServiceMock).get(EVENT_IS_NOT_EDITABLE),
                () -> verify(spaceServiceMock).findByIdValidated(spaceId),
                () -> verify(eventRepository).findBySpaceAndId(space, eventId)
        );
    }
}
