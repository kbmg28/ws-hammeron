package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.dto.event.EventSimpleDto;
import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.Event;
import br.com.kbmg.wsmusiccontrol.model.EventMusicAssociation;
import br.com.kbmg.wsmusiccontrol.model.Music;
import br.com.kbmg.wsmusiccontrol.repository.EventMusicAssociationRepository;
import br.com.kbmg.wsmusiccontrol.repository.projection.EventSimpleProjection;
import br.com.kbmg.wsmusiccontrol.service.EventMusicAssociationService;
import br.com.kbmg.wsmusiccontrol.service.MusicService;
import br.com.kbmg.wsmusiccontrol.util.mapper.MusicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventMusicAssociationServiceImpl extends GenericServiceImpl<EventMusicAssociation, EventMusicAssociationRepository> implements EventMusicAssociationService {

    @Autowired
    private MusicMapper musicMapper;

    @Autowired
    private MusicService musicService;

    @Override
    public Set<MusicWithSingerAndLinksDto> findAllMusicByEvent(Event event) {
        List<Music> list = repository.findAllMusicByEvent(event);
        return musicMapper.toMusicWithSingerAndLinksDtoList(list);
    }

    @Override
    public Set<EventMusicAssociation> createAssociation(Event event, Set<MusicWithSingerAndLinksDto> musicList) {
        Set<String> musicIdList = musicList.stream().map(MusicWithSingerAndLinksDto::getId).collect(Collectors.toSet());
        List<Music> entityMusicList = musicService.findAllById(musicIdList);

        if (entityMusicList.size() != musicIdList.size()) {
            throw new ServiceException(messagesService.get("event.music.list.invalid"));
        }

        return entityMusicList.stream().map(music -> {
            EventMusicAssociation eventMusicAssociation = new EventMusicAssociation(event, music);
            return repository.save(eventMusicAssociation);
        }).collect(Collectors.toSet());
    }

    @Override
    public List<EventSimpleDto> findEventsByMusic(Music music, Boolean eventsFromTheLast3Months) {
        List<EventSimpleProjection> projectionList;
        if (eventsFromTheLast3Months) {
            LocalDate now = LocalDate.now();
            LocalDate threeMothsAgo = now.minusMonths(3);
            projectionList = repository.findAllEventsOfMusicByDateRange(music.getId(), threeMothsAgo, now);
        } else {
            projectionList = repository.findAllEventsOfMusic(music.getId());
        }
        return projectionList
                .stream()
                .map(element -> new EventSimpleDto(element.getEventId(), element.getDate(), element.getName()))
                .collect(Collectors.toList());
    }
}
