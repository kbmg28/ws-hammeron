package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.UserPermission;

import java.util.List;

public interface UserPermissionService extends GenericService<UserPermission>{

    List<UserPermission> findBySpaceAndPermission(String spaceId, PermissionEnum permissionEnum);

    void addPermissionToUser(UserApp userApp, PermissionEnum permissionEnum);

    List<UserPermission> findAllSysAdmin();

    List<UserPermission> findAllByUserApp(UserApp userApp);

}
