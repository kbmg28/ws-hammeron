package br.com.kbmg.wshammeron.dto.event;

import br.com.kbmg.wshammeron.dto.music.MusicFullWithOrderDto;
import br.com.kbmg.wshammeron.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EventDetailsDto extends EventGenericDto {

    private Set<@Valid MusicFullWithOrderDto> musicList = new HashSet<>();
    private Set<@Valid UserDto> userList = new HashSet<>();

}
