package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.dto.event.EventDto;
import br.com.kbmg.wsmusiccontrol.dto.event.EventWithMusicListDto;
import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserDto;
import br.com.kbmg.wsmusiccontrol.enums.RangeDateFilterEnum;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.Event;
import br.com.kbmg.wsmusiccontrol.model.EventMusicAssociation;
import br.com.kbmg.wsmusiccontrol.model.EventSpaceUserAppAssociation;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.repository.EventRepository;
import br.com.kbmg.wsmusiccontrol.repository.projection.EventWithTotalAssociationsProjection;
import br.com.kbmg.wsmusiccontrol.service.EventMusicAssociationService;
import br.com.kbmg.wsmusiccontrol.service.EventService;
import br.com.kbmg.wsmusiccontrol.service.EventSpaceUserAppAssociationService;
import br.com.kbmg.wsmusiccontrol.service.SpaceService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public List<EventDto> findAllEventsBySpace(String spaceId, Boolean nextEvents, RangeDateFilterEnum rangeDateFilterEnum) {
        UserApp userLogged = userAppService.findUserLogged();
        Space space = spaceService.findByIdAndUserAppValidated(spaceId, userLogged);

        List<EventWithTotalAssociationsProjection> eventList = nextEvents ? findNextEvents(space, userLogged) : findOldEvents(space, rangeDateFilterEnum, userLogged);

        return eventList
                .stream()
                .map(this::parseToEventDto)
                .sorted(getSort(nextEvents))
                .collect(Collectors.toList());
    }

    private Comparator<EventDto> getSort(Boolean nextEvents) {
        Comparator<EventDto> eventListASC = Comparator.comparing(EventDto::getDate);

        return nextEvents ? eventListASC : eventListASC.reversed();
    }

    @Override
    public EventWithMusicListDto findBySpaceAndId(String spaceId, String eventId) {
        UserApp userLogged = userAppService.findUserLogged();
        spaceService.findByIdAndUserAppValidated(spaceId, userLogged);

        Event event = repository.findById(eventId)
                            .orElseThrow(() -> new ServiceException(
                                    messagesService.get("event.not.exist")
                            ));
        EventWithMusicListDto eventWithMusicListDto = new EventWithMusicListDto();

        eventWithMusicListDto.setDate(event.getDateEvent());
        eventWithMusicListDto.setTime(event.getTimeEvent());
        eventWithMusicListDto.setId(eventId);
        Set<UserDto> userList = eventSpaceUserAppAssociationService.findAllUserAppByEvent(event);
        eventWithMusicListDto.setUserList(userList);

        Set<MusicWithSingerAndLinksDto> musicList = eventMusicAssociationService.findAllMusicByEvent(event);

        eventWithMusicListDto.setMusicList(musicList);
        return eventWithMusicListDto;
    }

    @Override
    public EventDto createEvent(String spaceId, EventWithMusicListDto body) {
        UserApp userLogged = userAppService.findUserLogged();
        Space space = validateIfEventAlreadyExistAndGetSpace(spaceId, userLogged, body);

        Event event = new Event();
        event.setDateEvent(body.getDate());
        event.setTimeEvent(body.getTime());
        event.setSpace(space);

        repository.save(event);

        Set<EventMusicAssociation> musicList = saveMusicListOfEvent(body, event);
        Set<EventSpaceUserAppAssociation> userList = saveUserListOfEvent(body, space, event);

        boolean isUserLoggedIncluded = body.getUserList().stream().map(UserDto::getEmail).anyMatch(email -> userLogged.getEmail().equals(email));

        return new EventDto(event.getId(), event.getDateEvent(), event.getName(), event.getTimeEvent(), musicList.size(), userList.size(), isUserLoggedIncluded);
    }

    private Space validateIfEventAlreadyExistAndGetSpace(String spaceId, UserApp userLogged, EventWithMusicListDto body) {
        Space space = spaceService.findByIdAndUserAppValidated(spaceId, userLogged);
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
                .map(UserDto::getEmail)
                .collect(Collectors.toSet());

        Set<EventSpaceUserAppAssociation> userListAssociation = eventSpaceUserAppAssociationService
                .createAssociation(space, event, emailList);

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
