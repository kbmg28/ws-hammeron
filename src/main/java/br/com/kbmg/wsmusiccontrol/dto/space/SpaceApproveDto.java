package br.com.kbmg.wsmusiccontrol.dto.space;

import br.com.kbmg.wsmusiccontrol.enums.SpaceStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaceApproveDto {

    @NotNull
    private SpaceStatusEnum spaceStatusEnum;

}
