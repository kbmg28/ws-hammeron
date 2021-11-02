package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.model.Event;
import br.com.kbmg.wsmusiccontrol.model.EventMusicAssociation;
import br.com.kbmg.wsmusiccontrol.model.Music;
import br.com.kbmg.wsmusiccontrol.repository.EventMusicAssociationRepository;
import br.com.kbmg.wsmusiccontrol.service.EventMusicAssociationService;
import br.com.kbmg.wsmusiccontrol.util.mapper.MusicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class EventMusicAssociationServiceImpl extends GenericServiceImpl<EventMusicAssociation, EventMusicAssociationRepository> implements EventMusicAssociationService {

    @Autowired
    private MusicMapper musicMapper;

    @Override
    public Set<MusicWithSingerAndLinksDto> findAllMusicByEvent(Event event) {
        List<Music> list = repository.findAllMusicByEvent(event);
        return musicMapper.toMusicWithSingerAndLinksDtoList(list);
    }

    @Override
    public Set<EventMusicAssociation> createAssociation(Event event, Set<MusicWithSingerAndLinksDto> musicList) {
        // TODO: create logic to association
        return null;
    }
}
