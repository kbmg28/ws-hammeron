package br.com.kbmg.wshammeron.dto.space;

import br.com.kbmg.wshammeron.enums.SpaceStatusEnum;
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
