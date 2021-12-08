package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.user.UserWithPermissionDto;
import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.SpaceUserAppAssociation;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.UserPermission;

import java.util.List;
import java.util.Set;

public interface UserPermissionService extends GenericService<UserPermission>{

    void addPermissionToUser(SpaceUserAppAssociation spaceUserAppAssociation, PermissionEnum permissionEnum);

    List<String> findAllBySpaceAndUserApp(Space space, UserApp userApp);

    void checkPermissionsOfUsers(Space space, Set<UserWithPermissionDto> viewData);
}
