package br.com.kbmg.wshammeron.dto.space;

import br.com.kbmg.wshammeron.dto.user.UserDto;
import br.com.kbmg.wshammeron.enums.SpaceStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SpaceDto extends SpaceRequestDto{

    private String spaceId;

    @Valid
    private UserDto requestedBy;
    private LocalDateTime requestedByDate;

    @Valid
    private UserDto approvedBy;
    private LocalDateTime approvedByDate;

    private SpaceStatusEnum spaceStatus;

}
