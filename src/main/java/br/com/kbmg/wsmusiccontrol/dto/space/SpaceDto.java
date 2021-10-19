package br.com.kbmg.wsmusiccontrol.dto.space;

import br.com.kbmg.wsmusiccontrol.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SpaceDto extends SpaceRequestDto{

    private Long spaceId;

    private UserDto requestedBy;
    private LocalDateTime requestedByDate;

    private UserDto approvedBy;
    private LocalDateTime approvedByDate;
}
