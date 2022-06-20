package br.com.kbmg.wshammeron.dto.user;

import br.com.kbmg.wshammeron.model.UserApp;
import lombok.Data;

@Data
public class UserWithoutPermissionDto {

    private String id;
    private String name;
    private String email;
    private String cellPhone;

    public UserWithoutPermissionDto(UserApp userApp) {
        this.id = userApp.getId();
        this.name = userApp.getName();
        this.email = userApp.getEmail();
        this.cellPhone = userApp.getCellPhone();
    }
}
