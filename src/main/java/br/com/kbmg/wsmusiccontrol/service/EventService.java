package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.event.EventDto;
import br.com.kbmg.wsmusiccontrol.dto.event.EventWithMusicListDto;
import br.com.kbmg.wsmusiccontrol.enums.RangeDateFilterEnum;
import br.com.kbmg.wsmusiccontrol.model.Event;

import java.util.List;

public interface EventService extends GenericService<Event>{
    List<EventDto> findAllEventsBySpace(String spaceId, Boolean nextEvents, RangeDateFilterEnum rangeDateFilterEnum);

    EventWithMusicListDto findBySpaceAndId(String spaceId, String idMusic);

    EventDto createEvent(String spaceId, EventWithMusicListDto body);
}
