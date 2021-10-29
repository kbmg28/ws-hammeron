package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.model.Event;
import br.com.kbmg.wsmusiccontrol.model.EventMusicAssociation;

import java.util.Set;

public interface EventMusicAssociationService extends GenericService<EventMusicAssociation>{
    Set<MusicWithSingerAndLinksDto> findAllMusicByEvent(Event event);
}
