package br.com.kbmg.wshammeron.dto.event;

import br.com.kbmg.wshammeron.dto.music.MusicSimpleToEventDto;
import br.com.kbmg.wshammeron.dto.user.UserOnlyIdNameAndEmailDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventWithMusicListDto {

    private String id;

    private LocalDate date;

    @NotNull
    private OffsetDateTime utcDateTime;

    @NotBlank
    private String timeZoneName;

    @NotBlank
    private String name;

    @ApiModelProperty(example = "02:30")
    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalTime time;

    @Valid
    private Set<MusicSimpleToEventDto> musicList = new HashSet<>();

    @Valid
    private Set<UserOnlyIdNameAndEmailDto> userList = new HashSet<>();

}
