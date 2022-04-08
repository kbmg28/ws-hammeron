package br.com.kbmg.wshammeron.service;

import br.com.kbmg.wshammeron.dto.user.UserWithPermissionDto;
import br.com.kbmg.wshammeron.enums.PermissionEnum;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.SpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.model.UserPermission;

import java.util.List;
import java.util.Set;

public interface UserPermissionService extends GenericService<UserPermission>{

    void addPermissionToUser(SpaceUserAppAssociation spaceUserAppAssociation, PermissionEnum permissionEnum);

    List<String> findAllBySpaceAndUserApp(Space space, UserApp userApp);

    void checkPermissionsOfUsers(Space space, Set<UserWithPermissionDto> viewData);
}
