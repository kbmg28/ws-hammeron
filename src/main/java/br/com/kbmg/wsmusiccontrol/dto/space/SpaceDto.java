package br.com.kbmg.wsmusiccontrol.dto.space;

import br.com.kbmg.wsmusiccontrol.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SpaceDto extends SpaceRequestDto{

    private String spaceId;

    private UserDto requestedBy;
    private LocalDateTime requestedByDate;

    private UserDto approvedBy;
    private LocalDateTime approvedByDate;
}
