package br.com.kbmg.wshammeron.service;

import br.com.kbmg.wshammeron.dto.event.EventSimpleDto;
import br.com.kbmg.wshammeron.dto.music.MusicSimpleToEventDto;
import br.com.kbmg.wshammeron.model.Event;
import br.com.kbmg.wshammeron.model.EventMusicAssociation;
import br.com.kbmg.wshammeron.model.Music;

import java.util.List;
import java.util.Set;

public interface EventMusicAssociationService extends GenericService<EventMusicAssociation>{
    List<EventMusicAssociation> findAllMusicByEvent(Event event);

    Set<EventMusicAssociation> createAssociation(Event event, Set<MusicSimpleToEventDto> musicList);

    List<EventSimpleDto> findEventsByMusic(Music music, Boolean eventsFromTheLast3Months);

    Set<EventMusicAssociation> updateAssociations(Event eventInDatabase, Set<MusicSimpleToEventDto> musicList);
}
