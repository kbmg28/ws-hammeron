package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.dto.event.EventSimpleDto;
import br.com.kbmg.wsmusiccontrol.dto.music.MusicOnlyIdAndMusicNameAndSingerNameDto;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.Event;
import br.com.kbmg.wsmusiccontrol.model.EventMusicAssociation;
import br.com.kbmg.wsmusiccontrol.model.Music;
import br.com.kbmg.wsmusiccontrol.repository.EventMusicAssociationRepository;
import br.com.kbmg.wsmusiccontrol.repository.projection.EventSimpleProjection;
import br.com.kbmg.wsmusiccontrol.service.EventMusicAssociationService;
import br.com.kbmg.wsmusiccontrol.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EventMusicAssociationServiceImpl extends GenericServiceImpl<EventMusicAssociation, EventMusicAssociationRepository> implements EventMusicAssociationService {

    @Autowired
    private MusicService musicService;

    @Override
    public List<Music> findAllMusicByEvent(Event event) {
        List<Music> list = repository.findAllMusicByEvent(event);
        return list;
    }

    @Override
    public Set<EventMusicAssociation> createAssociation(Event event, Set<MusicOnlyIdAndMusicNameAndSingerNameDto> musicList) {
        Set<String> musicIdList = musicList.stream().map(MusicOnlyIdAndMusicNameAndSingerNameDto::getMusicId).collect(Collectors.toSet());
        List<Music> entityMusicList = musicService.findAllById(musicIdList);

        if (entityMusicList.size() != musicIdList.size()) {
            throw new ServiceException(messagesService.get("event.music.list.invalid"));
        }

        return createAssociationInDatabase(event, entityMusicList);
    }

    private Set<EventMusicAssociation> createAssociationInDatabase(Event event, List<Music> entityMusicList) {
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

    @Override
    public Set<EventMusicAssociation> updateAssociations(Event eventInDatabase, Set<MusicOnlyIdAndMusicNameAndSingerNameDto> musicList) {
        Set<EventMusicAssociation> musicListInDatabase = eventInDatabase.getEventMusicList();
        Map<String, EventMusicAssociation> musicInDatabaseMap = musicListInDatabase
                .stream()
                .collect(Collectors.toMap(ema -> ema.getMusic().getId(), Function.identity()));

        Set<String> musicIdList = musicList.stream().map(MusicOnlyIdAndMusicNameAndSingerNameDto::getMusicId).collect(Collectors.toSet());
        List<Music> musicUpdatedList = musicService.findAllById(musicIdList);

        List<Music> musicToCreateAssociationList = new ArrayList<>();

        musicUpdatedList.forEach(musicToUpdate -> {
            EventMusicAssociation eventMusicInDatabase = musicInDatabaseMap.get(musicToUpdate.getId());

            if (eventMusicInDatabase == null) {
                musicToCreateAssociationList.add(musicToUpdate);
            }

            musicInDatabaseMap.remove(musicToUpdate.getId());
        });

        if (musicInDatabaseMap.size() > 0) {
            Collection<EventMusicAssociation> eventUserAssociationList = musicInDatabaseMap.values();
            musicListInDatabase.removeAll(eventUserAssociationList);
            repository.deleteAllInBatch(eventUserAssociationList);
        }

        Set<EventMusicAssociation> newAssociations = createAssociationInDatabase(eventInDatabase, musicToCreateAssociationList);
        musicListInDatabase.addAll(newAssociations);

        return musicListInDatabase;
    }
}
