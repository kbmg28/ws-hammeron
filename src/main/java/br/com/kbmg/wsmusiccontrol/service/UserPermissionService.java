package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.model.UserPermission;

import java.util.List;

public interface UserPermissionService extends GenericService<UserPermission>{

    List<UserPermission> findByPermission(PermissionEnum permissionEnum);
}
