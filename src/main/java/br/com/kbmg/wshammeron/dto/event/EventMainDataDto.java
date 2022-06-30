package br.com.kbmg.wshammeron.dto.event;

import br.com.kbmg.wshammeron.dto.music.MusicSimpleToEventDto;
import br.com.kbmg.wshammeron.model.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EventMainDataDto extends EventGenericDto {

    private String timeZoneName;
    @Valid private Set<MusicSimpleToEventDto> musicList;

    public EventMainDataDto(Event event, Set<MusicSimpleToEventDto> musicList) {
        setId(event.getId());
        setUtcDateTime(event.getDateTimeEvent());
        setName(event.getName());

        this.timeZoneName = event.getTimeZoneName();
        this.musicList = musicList;
    }
}
