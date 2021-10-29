package br.com.kbmg.wsmusiccontrol.dto.event;

import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventWithMusicListDto {

    private String id;
    private LocalDate date;
    private LocalTime time;
    private Set<MusicWithSingerAndLinksDto> musicList = new HashSet<>();
    private Set<UserDto> userList = new HashSet<>();

}
