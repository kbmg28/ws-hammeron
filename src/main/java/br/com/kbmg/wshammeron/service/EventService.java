package br.com.kbmg.wshammeron.service;

import br.com.kbmg.wshammeron.dto.event.EventDetailsDto;
import br.com.kbmg.wshammeron.dto.event.EventDto;
import br.com.kbmg.wshammeron.dto.event.EventWithMusicListDto;
import br.com.kbmg.wshammeron.dto.space.overview.EventOverviewDto;
import br.com.kbmg.wshammeron.enums.RangeDateFilterEnum;
import br.com.kbmg.wshammeron.model.Event;
import br.com.kbmg.wshammeron.model.Space;

import java.time.OffsetDateTime;
import java.util.List;

public interface EventService extends GenericService<Event>{
    List<EventDto> findAllEventsBySpace(String spaceId, String hasMusicId, Boolean nextEvents, RangeDateFilterEnum rangeDateFilterEnum);

    EventDetailsDto findByIdValidated(String idMusic);

    EventDto createEvent(String spaceId, EventWithMusicListDto body);

    EventDto editEvent(String spaceId, String idEvent, EventWithMusicListDto body);

    List<EventOverviewDto> findEventOverviewBySpace(Space space);

    void deleteEvent(String spaceId, String idEvent);

    List<Event> findAllEventsByDateEvent(OffsetDateTime dateTimeEvent);

    void addOrRemoveMusicOnEvent(String spaceId, String idEvent, String musicId);
}
