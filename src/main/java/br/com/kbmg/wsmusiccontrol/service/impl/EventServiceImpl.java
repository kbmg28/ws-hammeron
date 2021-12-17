package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.config.security.SpringSecurityUtil;
import br.com.kbmg.wsmusiccontrol.dto.event.EventDetailsDto;
import br.com.kbmg.wsmusiccontrol.dto.event.EventDto;
import br.com.kbmg.wsmusiccontrol.dto.event.EventMainDataDto;
import br.com.kbmg.wsmusiccontrol.dto.event.EventWithMusicListDto;
import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.dto.space.overview.EventOverviewDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserOnlyIdNameAndEmailDto;
import br.com.kbmg.wsmusiccontrol.enums.DatabaseOperationEnum;
import br.com.kbmg.wsmusiccontrol.enums.EventTypeEnum;
import br.com.kbmg.wsmusiccontrol.enums.RangeDateFilterEnum;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.Event;
import br.com.kbmg.wsmusiccontrol.model.EventMusicAssociation;
import br.com.kbmg.wsmusiccontrol.model.EventSpaceUserAppAssociation;
import br.com.kbmg.wsmusiccontrol.model.Music;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.repository.EventRepository;
import br.com.kbmg.wsmusiccontrol.repository.projection.EventWithTotalAssociationsProjection;
import br.com.kbmg.wsmusiccontrol.repository.projection.OverviewProjection;
import br.com.kbmg.wsmusiccontrol.service.EventMusicAssociationService;
import br.com.kbmg.wsmusiccontrol.service.EventService;
import br.com.kbmg.wsmusiccontrol.service.EventSpaceUserAppAssociationService;
import br.com.kbmg.wsmusiccontrol.service.SpaceService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import br.com.kbmg.wsmusiccontrol.util.mapper.MusicMapper;
import br.com.kbmg.wsmusiccontrol.util.mapper.OverviewMapper;
import br.com.kbmg.wsmusiccontrol.util.mapper.UserAppMapper;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.EVENT_IS_NOT_EDITABLE;

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
    public List<EventDto> findAllEventsBySpace(String spaceId, Boolean nextEvents, RangeDateFilterEnum rangeDateFilterEnum) {
        UserApp userLogged = userAppService.findUserLogged();
        Space space = spaceService.findByIdValidated(spaceId);

        List<EventWithTotalAssociationsProjection> eventList = nextEvents ? findNextEvents(space, userLogged) : findOldEvents(space, rangeDateFilterEnum, userLogged);

        return eventList
                .stream()
                .map(this::parseToEventDto)
                .sorted(getSort(nextEvents))
                .collect(Collectors.toList());
    }

    @Override
    public EventDetailsDto findByIdValidated(String eventId) {
        Event event = repository.findById(eventId)
                            .orElseThrow(() -> new ServiceException(
                                    messagesService.get("event.not.exist")
                            ));
        EventDetailsDto eventDetails = new EventDetailsDto();

        eventDetails.setId(eventId);
        eventDetails.setName(event.getName());
        eventDetails.setDate(event.getDateEvent());
        eventDetails.setTime(event.getTimeEvent());

        findUserAssociation(event, eventDetails);
        findMusicAssociation(event, eventDetails);

        return eventDetails;
    }

    @Override
    public EventDto createEvent(String spaceId, EventWithMusicListDto body) {
        Space space = validateIfEventAlreadyExistAndGetSpace(spaceId, body);

        Event event = new Event();
        event.setDateEvent(body.getDate());
        event.setTimeEvent(body.getTime());
        event.setSpace(space);
        event.setName(body.getName());

        repository.save(event);

        Set<EventMusicAssociation> musicList = saveMusicListOfEvent(body, event);
        Set<EventSpaceUserAppAssociation> userList = saveUserListOfEvent(body, space, event);

        boolean isUserLoggedIncluded = isUserLoggedIncluded(body);

        return new EventDto(event.getId(), event.getDateEvent(), event.getName(), event.getTimeEvent(), musicList.size(), userList.size(), isUserLoggedIncluded);
    }

    @Override
    public EventDto editEvent(String spaceId, String idEvent, EventWithMusicListDto body) {
        Space space = spaceService.findByIdValidated(spaceId);
        Event eventInDatabase = this.findByIdEventAndSpaceValidated(idEvent, space);
        validateIfEventCanBeEdited(eventInDatabase);
        updateEventFields(eventInDatabase, body);

        Set<EventMusicAssociation> musicList =  eventMusicAssociationService.updateAssociations(eventInDatabase, body.getMusicList());
        Set<EventSpaceUserAppAssociation> userList = eventSpaceUserAppAssociationService.updateAssociations(eventInDatabase, body.getUserList(), body.getMusicList());

        boolean isUserLoggedIncluded = isUserLoggedIncluded(body);

        return new EventDto(eventInDatabase.getId(),
                eventInDatabase.getDateEvent(),
                eventInDatabase.getName(),
                eventInDatabase.getTimeEvent(),
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
        validateIfEventCanBeEdited(eventInDatabase);

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

    private void validateIfEventCanBeEdited(Event eventInDatabase) {
        LocalDate today = LocalDate.now();
        LocalDate currentDateOfEvent = eventInDatabase.getDateEvent();
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

    private Comparator<EventDto> getSort(Boolean nextEvents) {
        Comparator<EventDto> eventListASC = Comparator.comparing(EventDto::getDate);

        return nextEvents ? eventListASC : eventListASC.reversed();
    }

    private void findMusicAssociation(Event event, EventDetailsDto eventDetails) {
        List<Music> musicList = eventMusicAssociationService.findAllMusicByEvent(event);
        Set<MusicWithSingerAndLinksDto> dtoList = musicMapper.toMusicWithSingerAndLinksDtoList(musicList);
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
                .anyMatch(emailOfUserLogged::equals);
    }

    private void updateEventFields(Event eventInDatabase, EventWithMusicListDto body) {
        eventInDatabase.setName(body.getName());
        eventInDatabase.setDateEvent(body.getDate());
        eventInDatabase.setTimeEvent(body.getTime());
    }

    private Event findByIdEventAndSpaceValidated(String idEvent, Space space) {
        return repository.findBySpaceAndId(space, idEvent)
                .orElseThrow(() ->
                        new ServiceException(
                                messagesService.get("event.not.exist.space")
                        ));
    }

    private Space validateIfEventAlreadyExistAndGetSpace(String spaceId, EventWithMusicListDto body) {
        Space space = spaceService.findByIdValidated(spaceId);
        repository.findBySpaceAndDateEventAndTimeEvent(space, body.getDate(), body.getTime())
                .ifPresent(event -> {
                    throw new ServiceException(messagesService.get("event.already.exist"));
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
        dto.setDate(event.getDateEvent());
        dto.setTime(event.getTimeEvent());
        dto.setName(event.getNameEvent());
        dto.setId(event.getEventId());
        dto.setMusicQuantity(event.getMusicQuantity());
        dto.setUserQuantity(event.getUserQuantity());
        dto.setIsUserLoggedIncluded(event.getIsUserLoggedIncluded());
        return dto;
    }

    private List<EventWithTotalAssociationsProjection> findOldEvents(Space space, RangeDateFilterEnum rangeDateFilterEnum, UserApp userLogged) {
        if(rangeDateFilterEnum == null) {
            throw new ServiceException(messagesService.get("event.dateRange.required"));
        }
        LocalDate startDate = rangeDateFilterEnum.getStartOfRangeDateEvent();
        LocalDate endDate = LocalDate.now();

        return repository.findAllBySpaceAndDateEventRange(space.getId(), startDate, endDate, userLogged.getId());
    }

    private List<EventWithTotalAssociationsProjection> findNextEvents(Space space, UserApp userLogged) {
        LocalDate today = LocalDate.now();

        return repository.findAllBySpaceAndDateEventGreaterThanEqual(space.getId(), today, userLogged.getId());
    }

}
