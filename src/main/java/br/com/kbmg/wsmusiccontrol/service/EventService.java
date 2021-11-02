package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.event.EventWithMusicListDto;
import br.com.kbmg.wsmusiccontrol.model.Event;

import java.time.LocalDate;
import java.util.Set;

public interface EventService extends GenericService<Event>{
    Set<EventWithMusicListDto> findAllEventsBySpace(String spaceId, LocalDate startFilter, LocalDate endFilter);

    EventWithMusicListDto findBySpaceAndId(String spaceId, String idMusic);

    EventWithMusicListDto createEvent(String spaceId, EventWithMusicListDto body);
}
