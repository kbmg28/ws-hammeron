package builder;

import br.com.kbmg.wshammeron.dto.event.EventDetailsDto;
import br.com.kbmg.wshammeron.dto.event.EventDto;
import br.com.kbmg.wshammeron.dto.event.EventMainDataDto;
import br.com.kbmg.wshammeron.dto.event.EventWithMusicListDto;
import br.com.kbmg.wshammeron.dto.music.MusicFullWithOrderDto;
import br.com.kbmg.wshammeron.dto.music.MusicSimpleToEventDto;
import br.com.kbmg.wshammeron.dto.user.UserDto;
import br.com.kbmg.wshammeron.dto.user.UserOnlyIdNameAndEmailDto;
import br.com.kbmg.wshammeron.model.Event;
import br.com.kbmg.wshammeron.model.EventMusicAssociation;
import br.com.kbmg.wshammeron.model.EventSpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.Music;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.SpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.repository.projection.EventWithTotalAssociationsProjection;
import br.com.kbmg.wshammeron.util.DateUtilUTC;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static constants.BaseTestsConstants.ANY_VALUE;
import static org.springframework.util.CollectionUtils.isEmpty;

public abstract class EventBuilder {

    public static Event generateEvent(Space space, Boolean isNextEvent) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime dateTimeEvent = Boolean.TRUE.equals(isNextEvent) ? now.plusDays(1) : now.minusDays(1);

        Event event = new Event();

        event.setDateTimeEvent(dateTimeEvent);
        event.setTimeZoneName(now.toZonedDateTime().getZone().getId());
        event.setName(ANY_VALUE);

        event.setSpace(space);

        space.getEventList().add(event);
        return event;
    }

    public static EventMusicAssociation generateEventMusicAssociation(Event event, Music music) {
        Set<EventMusicAssociation> eventMusicList = event.getEventMusicList();
        Integer sequentialOrder = isEmpty(eventMusicList) ? 1 : eventMusicList.size() + 1;

        EventMusicAssociation eventMusicAssociation = new EventMusicAssociation(sequentialOrder, event, music);
        event.getEventMusicList().add(eventMusicAssociation);

        return eventMusicAssociation;
    }
    public static EventSpaceUserAppAssociation generateEventSpaceUserAppAssociation(Event event, SpaceUserAppAssociation spaceUserAppAssociation) {
        Set<EventSpaceUserAppAssociation> eventSpaceUserAppAssociationList = event.getSpaceUserAppAssociationList();

        EventSpaceUserAppAssociation eventSpaceUserAppAssociation = new EventSpaceUserAppAssociation(event, spaceUserAppAssociation);
        eventSpaceUserAppAssociationList.add(eventSpaceUserAppAssociation);

        return eventSpaceUserAppAssociation;
    }

    public static EventDto generateEventDto(Event event) {
        return new EventDto(){{
            setId(event.getId());
            setName(event.getName());
            setUtcDateTime(event.getDateTimeEvent());
            setMusicQuantity(event.getEventMusicList().size());
            setUserQuantity(event.getSpaceUserAppAssociationList().size());
            setIsUserLoggedIncluded(true);
        }};
    }

    public static EventMainDataDto generateEventMainDataDto(Event event) {
        return new EventMainDataDto(){{
            setId(event.getId());
            setName(event.getName());
            setUtcDateTime(event.getDateTimeEvent());
            setTimeZoneName(event.getTimeZoneName());
            setMusicList(new HashSet<>());
        }};
    }

    public static EventDetailsDto generateEventDetailsDto(Event event, UserApp userApp) {
        Set<MusicFullWithOrderDto> musicsOfEvent = event.getEventMusicList()
                .stream()
                .map(eventMusicAssociation -> {
                    Music music = eventMusicAssociation.getMusic();
                    return MusicBuilder.generateMusicFullWithOrderDto(music, eventMusicAssociation.getSequentialOrder());
                })
                .collect(Collectors.toSet());

        UserDto userDto = UserBuilder.generateUserDto(userApp.getEmail());
        return new EventDetailsDto(){{
            setId(event.getId());
            setName(event.getName());
            setUtcDateTime(event.getDateTimeEvent());
            setMusicList(musicsOfEvent);
            setUserList(Set.of(userDto));
        }};
    }

    public static EventWithMusicListDto generateEventWithMusicListDto(Event event, UserApp userApp) {
        Set<MusicSimpleToEventDto> musicsOfEvent = event.getEventMusicList()
                .stream()
                .map(eventMusicAssociation -> {
                    Music music = eventMusicAssociation.getMusic();
                    return MusicBuilder.generateMusicSimpleToEventDto(music, eventMusicAssociation.getSequentialOrder());
                })
                .collect(Collectors.toSet());

        UserOnlyIdNameAndEmailDto userOnlyIdNameAndEmailDto = UserBuilder.generateUserOnlyIdNameAndEmailDto(userApp);
        return new EventWithMusicListDto(){{
            setId(event.getId());
            setName(event.getName());
            setUtcDateTime(event.getDateTimeEvent());
            setMusicList(musicsOfEvent);
            setUserList(Set.of(userOnlyIdNameAndEmailDto));
        }};
    }

    public static EventWithTotalAssociationsProjection generateEventWithTotalAssociationsProjection(Event event) {
        return new EventWithTotalAssociationsProjection(){
            @Override
            public String getEventId() {
                return event.getId();
            }

            @Override
            public String getNameEvent() {
                return event.getName();
            }

            @Override
            public Timestamp getDateTimeEvent() {
                return DateUtilUTC.toTimestamp(event.getDateTimeEvent());
            }

            @Override
            public Integer getMusicQuantity() {
                return event.getEventMusicList().size();
            }

            @Override
            public Integer getUserQuantity() {
                return event.getSpaceUserAppAssociationList().size();
            }

            @Override
            public Boolean getIsUserLoggedIncluded() {
                return true;
            }
            {}
        };
    }

}
