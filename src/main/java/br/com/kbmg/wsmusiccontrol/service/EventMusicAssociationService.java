package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.event.EventSimpleDto;
import br.com.kbmg.wsmusiccontrol.dto.music.MusicOnlyIdAndMusicNameAndSingerNameDto;
import br.com.kbmg.wsmusiccontrol.model.Event;
import br.com.kbmg.wsmusiccontrol.model.EventMusicAssociation;
import br.com.kbmg.wsmusiccontrol.model.Music;

import java.util.List;
import java.util.Set;

public interface EventMusicAssociationService extends GenericService<EventMusicAssociation>{
    List<Music> findAllMusicByEvent(Event event);

    Set<EventMusicAssociation> createAssociation(Event event, Set<MusicOnlyIdAndMusicNameAndSingerNameDto> musicList);

    List<EventSimpleDto> findEventsByMusic(Music music, Boolean eventsFromTheLast3Months);
}
