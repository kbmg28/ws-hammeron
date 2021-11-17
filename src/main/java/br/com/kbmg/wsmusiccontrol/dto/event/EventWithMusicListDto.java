package br.com.kbmg.wsmusiccontrol.dto.event;

import br.com.kbmg.wsmusiccontrol.dto.music.MusicOnlyIdAndMusicNameAndSingerNameDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserOnlyIdNameAndEmailDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
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
    private String name;

    @ApiModelProperty(example = "02:30")
    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalTime time;

    private Set<MusicOnlyIdAndMusicNameAndSingerNameDto> musicList = new HashSet<>();
    private Set<UserOnlyIdNameAndEmailDto> userList = new HashSet<>();

}
