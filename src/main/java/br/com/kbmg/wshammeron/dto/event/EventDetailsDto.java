package br.com.kbmg.wshammeron.dto.event;

import br.com.kbmg.wshammeron.dto.music.MusicFullWithOrderDto;
import br.com.kbmg.wshammeron.dto.user.UserDto;
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
public class EventDetailsDto {

    private String id;
    private LocalDate date;
    private String name;

    @ApiModelProperty(example = "02:30")
    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalTime time;

    private Set<MusicFullWithOrderDto> musicList = new HashSet<>();
    private Set<UserDto> userList = new HashSet<>();

}
