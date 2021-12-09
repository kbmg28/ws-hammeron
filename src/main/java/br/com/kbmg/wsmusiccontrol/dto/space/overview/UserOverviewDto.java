package br.com.kbmg.wsmusiccontrol.dto.space.overview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOverviewDto {

    private String permissionName;
    private Long total;

}
