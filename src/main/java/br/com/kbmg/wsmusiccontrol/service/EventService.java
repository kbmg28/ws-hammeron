package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.event.EventDetailsDto;
import br.com.kbmg.wsmusiccontrol.dto.event.EventDto;
import br.com.kbmg.wsmusiccontrol.dto.event.EventWithMusicListDto;
import br.com.kbmg.wsmusiccontrol.dto.space.overview.EventOverviewDto;
import br.com.kbmg.wsmusiccontrol.enums.RangeDateFilterEnum;
import br.com.kbmg.wsmusiccontrol.model.Event;
import br.com.kbmg.wsmusiccontrol.model.Space;

import java.time.LocalDate;
import java.util.List;

public interface EventService extends GenericService<Event>{
    List<EventDto> findAllEventsBySpace(String spaceId, Boolean nextEvents, RangeDateFilterEnum rangeDateFilterEnum);

    EventDetailsDto findByIdValidated(String idMusic);

    EventDto createEvent(String spaceId, EventWithMusicListDto body);

    EventDto editEvent(String spaceId, String idEvent, EventWithMusicListDto body);

    List<EventOverviewDto> findEventOverviewBySpace(Space space);

    void deleteEvent(String spaceId, String idEvent);

    List<Event> findAllEventsByDateEvent(LocalDate today);
}
