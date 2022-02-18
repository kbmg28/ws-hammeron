package br.com.kbmg.wsmusiccontrol.dto.event;

import br.com.kbmg.wsmusiccontrol.dto.music.MusicOnlyIdAndMusicNameAndSingerNameDto;
import br.com.kbmg.wsmusiccontrol.model.Event;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Data
public class EventMainDataDto {

    private String id;
    private LocalDate dateEvent;
    private LocalTime timeEvent;
    private String timeZoneName;
    private String nameEvent;

    private Set<MusicOnlyIdAndMusicNameAndSingerNameDto> musicList;

    public EventMainDataDto(Event event, Set<MusicOnlyIdAndMusicNameAndSingerNameDto> musicList) {
        this.id = event.getId();
        this.dateEvent = event.getDateEvent();
        this.timeEvent = event.getTimeEvent();
        this.timeZoneName = event.getTimeZoneName();
        this.nameEvent = event.getName();
        this.musicList = musicList;
    }
}
