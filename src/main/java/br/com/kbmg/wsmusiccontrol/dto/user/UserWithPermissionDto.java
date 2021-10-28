package br.com.kbmg.wsmusiccontrol.dto.user;

import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithPermissionDto {

    private String id;
    private String name;
    private String email;
    private String cellPhone;
    private Set<PermissionEnum> permissionList;

}
