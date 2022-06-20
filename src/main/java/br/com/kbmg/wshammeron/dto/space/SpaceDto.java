package br.com.kbmg.wshammeron.dto.space;

import br.com.kbmg.wshammeron.dto.user.UserDto;
import br.com.kbmg.wshammeron.enums.SpaceStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SpaceDto extends SpaceRequestDto{

    private String spaceId;

    private UserDto requestedBy;
    private LocalDateTime requestedByDate;

    private UserDto approvedBy;
    private LocalDateTime approvedByDate;

    private SpaceStatusEnum spaceStatus;

}
