package br.com.kbmg.wshammeron.service.impl;

import br.com.kbmg.wshammeron.config.security.SpringSecurityUtil;
import br.com.kbmg.wshammeron.dto.event.EventDetailsDto;
import br.com.kbmg.wshammeron.dto.event.EventDto;
import br.com.kbmg.wshammeron.dto.event.EventMainDataDto;
import br.com.kbmg.wshammeron.dto.event.EventWithMusicListDto;
import br.com.kbmg.wshammeron.dto.music.MusicFullWithOrderDto;
import br.com.kbmg.wshammeron.dto.space.overview.EventOverviewDto;
import br.com.kbmg.wshammeron.dto.user.UserDto;
import br.com.kbmg.wshammeron.dto.user.UserOnlyIdNameAndEmailDto;
import br.com.kbmg.wshammeron.enums.DatabaseOperationEnum;
import br.com.kbmg.wshammeron.enums.EventTypeEnum;
import br.com.kbmg.wshammeron.enums.RangeDateFilterEnum;
import br.com.kbmg.wshammeron.exception.ServiceException;
import br.com.kbmg.wshammeron.model.Event;
import br.com.kbmg.wshammeron.model.EventMusicAssociation;
import br.com.kbmg.wshammeron.model.EventSpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.repository.EventRepository;
import br.com.kbmg.wshammeron.repository.projection.EventWithTotalAssociationsProjection;
import br.com.kbmg.wshammeron.repository.projection.OverviewProjection;
import br.com.kbmg.wshammeron.service.EventMusicAssociationService;
import br.com.kbmg.wshammeron.service.EventService;
import br.com.kbmg.wshammeron.service.EventSpaceUserAppAssociationService;
import br.com.kbmg.wshammeron.service.SpaceService;
import br.com.kbmg.wshammeron.service.UserAppService;
import br.com.kbmg.wshammeron.util.DateUtilUTC;
import br.com.kbmg.wshammeron.util.mapper.MusicMapper;
import br.com.kbmg.wshammeron.util.mapper.OverviewMapper;
import br.com.kbmg.wshammeron.util.mapper.UserAppMapper;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.EVENT_ALREADY_EXIST;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.EVENT_CREATE_DATETIME_INVALID;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.EVENT_DATE_RANGE_REQUIRED;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.EVENT_DO_NOT_EXIST;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.EVENT_DO_NOT_EXIST_ON_SPACE;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.EVENT_IS_NOT_EDITABLE;

@Service
public class EventServiceImpl extends GenericServiceImpl<Event, EventRepository> implements EventService {

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private EventSpaceUserAppAssociationService eventSpaceUserAppAssociationService;

    @Autowired
    private EventMusicAssociationService eventMusicAssociationService;

    @Autowired
    private UserAppMapper userAppMapper;

    @Autowired
    private MusicMapper musicMapper;

    @Autowired
    private OverviewMapper overviewMapper;

    @Override
    public List<EventDto> findAllEventsBySpace(String spaceId, String hasMusicId, Boolean nextEvents, RangeDateFilterEnum rangeDateFilterEnum) {
        UserApp userLogged = userAppService.findUserLogged();
        Space space = spaceService.findByIdValidated(spaceId);

        List<EventWithTotalAssociationsProjection> eventList = Boolean.TRUE.equals(nextEvents) ?
                findNextEvents(space, hasMusicId, userLogged) :
                findOldEvents(space, rangeDateFilterEnum, userLogged);

        return eventList
                .stream()
                .map(this::parseToEventDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventDetailsDto findByIdValidated(String eventId) {
        Event event = repository.findById(eventId)
                            .orElseThrow(() -> new ServiceException(
                                    messagesService.get(EVENT_DO_NOT_EXIST)
                            ));

        EventDetailsDto eventDetails = new EventDetailsDto();

        eventDetails.setId(eventId);
        eventDetails.setName(event.getName());
        eventDetails.setUtcDateTime(event.getDateTimeEvent());

        findUserAssociation(event, eventDetails);
        findMusicAssociation(event, eventDetails);

        return eventDetails;
    }

    @Override
    public EventDto createEvent(String spaceId, @Valid EventWithMusicListDto body) {
        validateIfDateTimeEventGreaterNow(body);
        Space space = validateIfEventAlreadyExistAndGetSpace(spaceId, body);

        Event event = new Event();
        event.setSpace(space);
        event.setTimeZoneName(body.getTimeZoneName());

        updateEventFields(event, body);

        repository.save(event);

        Set<EventMusicAssociation> musicList = saveMusicListOfEvent(body, event);
        Set<EventSpaceUserAppAssociation> userList = saveUserListOfEvent(body, space, event);

        boolean isUserLoggedIncluded = isUserLoggedIncluded(body);

        return new EventDto(event.getId(),
                event.getName(),
                event.getDateTimeEvent(),
                musicList.size(),
                userList.size(),
                isUserLoggedIncluded);
    }

    @Override
    public EventDto editEvent(String spaceId, String idEvent, @Valid EventWithMusicListDto body) {
        Space space = spaceService.findByIdValidated(spaceId);
        Event eventInDatabase = this.findByIdEventAndSpaceValidated(idEvent, space);
        validateDateTimeToEditOrDelete(body.getUtcDateTime());
        updateEventFields(eventInDatabase, body);

        Set<EventMusicAssociation> musicList =  eventMusicAssociationService.updateAssociations(eventInDatabase, body.getMusicList());
        Set<EventSpaceUserAppAssociation> userList = eventSpaceUserAppAssociationService.updateAssociations(eventInDatabase, body.getUserList(), body.getMusicList());

        boolean isUserLoggedIncluded = isUserLoggedIncluded(body);

        return new EventDto(eventInDatabase.getId(),
                eventInDatabase.getName(),
                eventInDatabase.getDateTimeEvent(),
                musicList.size(),
                userList.size(),
                isUserLoggedIncluded);
    }

    @Override
    public List<EventOverviewDto> findEventOverviewBySpace(Space space) {
        List<OverviewProjection> list = repository.findEventOverviewBySpace(space.getId());

        List<EventOverviewDto> eventOverviewDtoList = overviewMapper.toEventOverviewDtoList(list);
        Map<String, List<EventOverviewDto>> eventOverviewMap = eventOverviewDtoList.stream().collect(Collectors.groupingBy(EventOverviewDto::getEventType));
        Arrays.asList(EventTypeEnum.values()).forEach(type -> {
            String typeEvent = type.name();
            if(!eventOverviewMap.containsKey(typeEvent)) {
                eventOverviewDtoList.add(new EventOverviewDto(typeEvent, 0L));
            }
        });
        return eventOverviewDtoList;
    }

    @Override
    public void deleteEvent(String spaceId, String idEvent) {
        Space space = spaceService.findByIdValidated(spaceId);
        Event eventInDatabase = this.findByIdEventAndSpaceValidated(idEvent, space);
        validateDateTimeToEditOrDelete(eventInDatabase.getDateTimeEvent());

        EventMainDataDto eventMainDataDto = new EventMainDataDto(eventInDatabase, null);

        Set<EventMusicAssociation> eventMusicList = eventInDatabase.getEventMusicList();
        Set<EventSpaceUserAppAssociation> spaceUserAppAssociationList = eventInDatabase.getSpaceUserAppAssociationList();

        Set<UserApp> userList = spaceUserAppAssociationList
                .stream()
                .map(esu -> esu.getSpaceUserAppAssociation().getUserApp())
                .collect(Collectors.toSet());

        logInfoMusicDelete("START", eventInDatabase, eventMusicList.size());
        eventMusicAssociationService.deleteInBatch(eventMusicList);
        logInfoMusicDelete("END", eventInDatabase, eventMusicList.size());


        logInfoUserAssociationDelete("START", eventInDatabase, spaceUserAppAssociationList.size());
        eventSpaceUserAppAssociationService.deleteInBatch(spaceUserAppAssociationList);
        logInfoUserAssociationDelete("END", eventInDatabase, spaceUserAppAssociationList.size());

        repository.delete(eventInDatabase);

        eventSpaceUserAppAssociationService.sendNotificationToAssociations(eventMainDataDto,
                userList,
                DatabaseOperationEnum.DELETE);
    }

    @Override
    public List<Event> findAllEventsByDateEvent(OffsetDateTime dateTimeEvent) {
        return repository.findAllEventsByDateTimeEvent(dateTimeEvent);
    }

    private void validateIfDateTimeEventGreaterNow(EventWithMusicListDto body) {
        OffsetDateTime now = OffsetDateTime.now();

        if (body.getUtcDateTime().isBefore(now)) {
            throw new ServiceException(messagesService.get(EVENT_CREATE_DATETIME_INVALID));
        }
    }

    private void validateDateTimeToEditOrDelete(OffsetDateTime currentDateOfEvent) {
        OffsetDateTime today = OffsetDateTime.now().minusHours(2);

        if(today.isAfter(currentDateOfEvent)){
            throw new ServiceException(messagesService.get(EVENT_IS_NOT_EDITABLE));
        }

    }

    private void logInfoMusicDelete(String type, Event eventInDatabase, int size) {
        logService.logMessage(Level.INFO, String.format(
                "[%s] Delete %d music associations of event: '%s' (%s)",
                    type,
                    size,
                    eventInDatabase.getName(),
                    eventInDatabase.getId()
                )
        );
    }

    private void logInfoUserAssociationDelete(String type, Event eventInDatabase, int size) {
        logService.logMessage(Level.INFO, String.format(
                "[%s] Delete %d user associations of event: '%s' (%s)",
                    type,
                    size,
                    eventInDatabase.getName(),
                    eventInDatabase.getId()
                )
        );
    }

    private void findMusicAssociation(Event event, EventDetailsDto eventDetails) {
        List<EventMusicAssociation> associationList = eventMusicAssociationService.findAllMusicByEvent(event);
        Set<MusicFullWithOrderDto> dtoList = musicMapper
                .toMusicFullWithOrderDtoList(associationList)
                .stream()
                .sorted(Comparator.comparingInt(MusicFullWithOrderDto::getSequentialOrder))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        eventDetails.setMusicList(dtoList);
    }

    private void findUserAssociation(Event event, EventDetailsDto eventDetails) {
        List<UserApp> userEntityList = eventSpaceUserAppAssociationService.findAllUserAppByEvent(event);
        Set<UserDto> userList = userAppMapper.toUserDtoList(userEntityList);
        eventDetails.setUserList(userList);
    }

    private boolean isUserLoggedIncluded(EventWithMusicListDto body) {
        String emailOfUserLogged = SpringSecurityUtil.getEmail();
        return body
                .getUserList()
                .stream()
                .map(UserOnlyIdNameAndEmailDto::getEmail)
                .anyMatch(email -> emailOfUserLogged != null && emailOfUserLogged.equals(email));
    }

    private void updateEventFields(Event eventInDatabase, EventWithMusicListDto body) {
        eventInDatabase.setName(body.getName());
        eventInDatabase.setDateTimeEvent(body.getUtcDateTime());
    }

    private Event findByIdEventAndSpaceValidated(String idEvent, Space space) {
        return repository.findBySpaceAndId(space, idEvent)
                .orElseThrow(() ->
                        new ServiceException(
                                messagesService.get(EVENT_DO_NOT_EXIST_ON_SPACE)
                        ));
    }

    private Space validateIfEventAlreadyExistAndGetSpace(String spaceId, EventWithMusicListDto body) {
        Space space = spaceService.findByIdValidated(spaceId);
        repository.findBySpaceAndDateTimeEvent(space, body.getUtcDateTime())
                .ifPresent(event -> {
                    throw new ServiceException(messagesService.get(EVENT_ALREADY_EXIST));
                });

        return space;
    }

    private Set<EventMusicAssociation> saveMusicListOfEvent(EventWithMusicListDto body, Event event) {
        Set<EventMusicAssociation> list = eventMusicAssociationService
                .createAssociation(event, body.getMusicList());
        event.setEventMusicList(list);
        return list;
    }

    private Set<EventSpaceUserAppAssociation> saveUserListOfEvent(EventWithMusicListDto body, Space space, Event event) {
        Set<String> emailList = body
                .getUserList()
                .stream()
                .map(UserOnlyIdNameAndEmailDto::getEmail)
                .collect(Collectors.toSet());

        Set<EventSpaceUserAppAssociation> userListAssociation = eventSpaceUserAppAssociationService
                .createAssociation(space, event, emailList, body.getMusicList());

        event.setSpaceUserAppAssociationList(userListAssociation);
        return userListAssociation;
    }

    private EventDto parseToEventDto(EventWithTotalAssociationsProjection event) {
        EventDto dto = new EventDto();
        dto.setUtcDateTime(DateUtilUTC.toOffsetDateTime(event.getDateTimeEvent()));
        dto.setName(event.getNameEvent());
        dto.setId(event.getEventId());
        dto.setMusicQuantity(event.getMusicQuantity());
        dto.setUserQuantity(event.getUserQuantity());
        dto.setIsUserLoggedIncluded(event.getIsUserLoggedIncluded());
        dto.setHasMusicId(event.getHasMusicId());
        return dto;
    }

    private List<EventWithTotalAssociationsProjection> findOldEvents(Space space, RangeDateFilterEnum rangeDateFilterEnum, UserApp userLogged) {
        if(rangeDateFilterEnum == null) {
            throw new ServiceException(messagesService.get(EVENT_DATE_RANGE_REQUIRED));
        }
        OffsetDateTime now = OffsetDateTime.now().minusHours(2);

        OffsetDateTime startDate = rangeDateFilterEnum.getStartOfRangeDateEvent();

        return repository.findAllBySpaceAndDateTimeEventRange(space.getId(), startDate, now, userLogged.getId());
    }

    private List<EventWithTotalAssociationsProjection> findNextEvents(Space space, String hasMusicId, UserApp userLogged) {
        OffsetDateTime now = OffsetDateTime.now().minusHours(2);

        return repository.findAllBySpaceAndDateTimeEventGreaterThanEqual(space.getId(), now, userLogged.getId(), hasMusicId);
    }

}
