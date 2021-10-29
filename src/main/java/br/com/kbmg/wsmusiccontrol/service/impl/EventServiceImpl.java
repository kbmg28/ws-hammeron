package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.dto.event.EventWithMusicListDto;
import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserDto;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.Event;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.repository.EventRepository;
import br.com.kbmg.wsmusiccontrol.service.EventMusicAssociationService;
import br.com.kbmg.wsmusiccontrol.service.EventService;
import br.com.kbmg.wsmusiccontrol.service.EventSpaceUserAppAssociationService;
import br.com.kbmg.wsmusiccontrol.service.SpaceService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

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
    public Set<EventWithMusicListDto> findAllEventsBySpace(String spaceId, LocalDate startFilter, LocalDate endFilter) {
        return null;
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

        eventWithMusicListDto.setDate(event.getDate());
        eventWithMusicListDto.setTime(event.getTime());
        eventWithMusicListDto.setId(eventId);
        Set<UserDto> userList = eventSpaceUserAppAssociationService.findAllUserAppByEvent(event);
        eventWithMusicListDto.setUserList(userList);

        Set<MusicWithSingerAndLinksDto> musicList = eventMusicAssociationService.findAllMusicByEvent(event);

        eventWithMusicListDto.setMusicList(musicList);
        return eventWithMusicListDto;
    }

}
