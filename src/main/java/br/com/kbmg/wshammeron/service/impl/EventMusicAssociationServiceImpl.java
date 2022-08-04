package br.com.kbmg.wshammeron.service.impl;

import br.com.kbmg.wshammeron.dto.event.EventSimpleDto;
import br.com.kbmg.wshammeron.dto.music.MusicSimpleToEventDto;
import br.com.kbmg.wshammeron.exception.ServiceException;
import br.com.kbmg.wshammeron.model.Event;
import br.com.kbmg.wshammeron.model.EventMusicAssociation;
import br.com.kbmg.wshammeron.model.Music;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.repository.EventMusicAssociationRepository;
import br.com.kbmg.wshammeron.repository.projection.EventSimpleProjection;
import br.com.kbmg.wshammeron.service.EventMusicAssociationService;
import br.com.kbmg.wshammeron.service.MusicService;
import br.com.kbmg.wshammeron.util.DateUtilUTC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
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
    public List<EventMusicAssociation> findAllMusicByEvent(Event event) {
        return repository.findAllByEvent(event);
    }

    @Override
    public Set<EventMusicAssociation> createAssociation(Event event, Set<MusicSimpleToEventDto> musicList) {
        Map<String, Integer> musicIdOrderMap = getMusicIdOrderMap(musicList);
        Set<String> musicIdList = musicIdOrderMap.keySet();

        List<Music> entityMusicList = musicService.findAllById(musicIdList);

        if (entityMusicList.size() != musicIdList.size()) {
            throw new ServiceException(messagesService.get("event.music.list.invalid"));
        }

        return createAssociationInDatabase(event, entityMusicList, musicIdOrderMap);
    }

    @Override
    public List<EventSimpleDto> findEventsByMusic(Music music, Boolean eventsFromTheLast3Months) {
        List<EventSimpleProjection> projectionList;
        if (Boolean.TRUE.equals(eventsFromTheLast3Months)) {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime threeMothsAgo = now.minusMonths(3);

            projectionList = repository.findAllEventsOfMusicByDateRange(music.getId(), threeMothsAgo, now);
        } else {
            projectionList = repository.findAllEventsOfMusic(music.getId());
        }
        return projectionList
                .stream()
                .map(element -> new EventSimpleDto(element.getEventId(),
                        element.getName(),
                        DateUtilUTC.toOffsetDateTime(element.getDateTimeEvent())))
                .collect(Collectors.toList());
    }

    @Override
    public Set<EventMusicAssociation> updateAssociations(Event eventInDatabase, Set<MusicSimpleToEventDto> musicList) {
        Set<EventMusicAssociation> musicListInDatabase = eventInDatabase.getEventMusicList();
        Map<String, EventMusicAssociation> musicInDatabaseMap = musicListInDatabase
                .stream()
                .collect(Collectors.toMap(ema -> ema.getMusic().getId(), Function.identity()));

        Map<String, Integer> musicIdOrderMap = getMusicIdOrderMap(musicList);
        Set<String> musicIdList = musicIdOrderMap.keySet();
        List<Music> musicUpdatedList = musicService.findAllById(musicIdList);

        List<Music> musicToCreateAssociationList = new ArrayList<>();

        musicUpdatedList.forEach(musicToUpdate -> {
            String currentMusicId = musicToUpdate.getId();

            EventMusicAssociation eventMusicInDatabase = musicInDatabaseMap.get(currentMusicId);

            if (eventMusicInDatabase == null) {
                musicToCreateAssociationList.add(musicToUpdate);
            } else {
                eventMusicInDatabase.setSequentialOrder(musicIdOrderMap.get(currentMusicId));
            }

            musicInDatabaseMap.remove(currentMusicId);
        });

        if (musicInDatabaseMap.size() > 0) {
            Collection<EventMusicAssociation> eventUserAssociationList = musicInDatabaseMap.values();
            musicListInDatabase.removeAll(eventUserAssociationList);
            repository.deleteAllInBatch(eventUserAssociationList);
        }

        Set<EventMusicAssociation> newAssociations =
                createAssociationInDatabase(eventInDatabase, musicToCreateAssociationList, musicIdOrderMap);
        musicListInDatabase.addAll(newAssociations);

        return musicListInDatabase;
    }

    @Override
    public void addOrRemoveMusicOnEvent(String musicId, Space space, Event eventInDatabase) {
        Music music = musicService.findBySpaceAndId(space, musicId);

        repository.findByEventAndMusic(eventInDatabase, music)
                .ifPresentOrElse(
                        repository::delete,
                        () -> associateMusicWithLastOrderOfEvent(eventInDatabase, music)
                );
    }

    private void associateMusicWithLastOrderOfEvent(Event eventInDatabase, Music music) {
        long quantityMusicsOfEvent = repository.countByEvent(eventInDatabase);
        long lastOrder = quantityMusicsOfEvent + 1;
        createAssociationInDatabase(eventInDatabase, music, (int) lastOrder);
    }

    private Map<String, Integer> getMusicIdOrderMap(Set<MusicSimpleToEventDto> musicList) {
        return musicList.stream().collect(Collectors
                .toMap(MusicSimpleToEventDto::getMusicId,
                        MusicSimpleToEventDto::getSequentialOrder));
    }

    private Set<EventMusicAssociation> createAssociationInDatabase(Event event, List<Music> entityMusicList, Map<String, Integer> musicIdOrderMap) {
        return entityMusicList.stream().map(music -> {
            Integer sequentialOrder = musicIdOrderMap.get(music.getId());
            return createAssociationInDatabase(event, music, sequentialOrder);
        }).collect(Collectors.toSet());
    }

    private EventMusicAssociation createAssociationInDatabase(Event event, Music music, Integer sequentialOrder) {
        EventMusicAssociation eventMusicAssociation = new EventMusicAssociation(sequentialOrder, event, music);
        return repository.save(eventMusicAssociation);
    }

}
