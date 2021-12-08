package br.com.kbmg.wsmusiccontrol.dto.user;

import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithSinglePermissionDto {

    private String email;
    private PermissionEnum permission;
}
