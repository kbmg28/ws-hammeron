package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.exception.ForbiddenException;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.UserPermission;
import br.com.kbmg.wsmusiccontrol.repository.UserPermissionRepository;
import br.com.kbmg.wsmusiccontrol.service.SpaceService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import br.com.kbmg.wsmusiccontrol.service.UserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserPermissionServiceImpl
        extends GenericServiceImpl<UserPermission, UserPermissionRepository>
        implements UserPermissionService {

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private SpaceService spaceService;

    @Override
    public List<UserPermission> findBySpaceAndPermission(String spaceId, PermissionEnum permissionEnum) {
        UserApp userLogged = userAppService.findUserLogged();
        Space space = spaceService.findByIdAndUserAppValidated(spaceId, userLogged);
        verifyIfUserLoggedIsSuperUserAndArgumentIsSuperUser(userLogged, permissionEnum);

        return repository.findByPermissionAndSpace(permissionEnum, space);
    }

    @Override
    public void addPermissionToUser(UserApp userApp, PermissionEnum permissionEnum) {
        Set<UserPermission> userPermissionList = userApp.getUserPermissionList();

        boolean hasNoPermission = hasNoPermission(permissionEnum, userPermissionList);

        if(hasNoPermission) {
            UserApp userLogged = userAppService.findUserLogged();

            if (userLogged != null) {
                verifyIfUserLoggedCanExecuteTheAction(userLogged, permissionEnum);
            }

            UserPermission newUserPermission = new UserPermission();
            newUserPermission.setPermission(permissionEnum);
            newUserPermission.setUserApp(userApp);

            repository.save(newUserPermission);

            userPermissionList.add(newUserPermission);
        }
    }

    @Override
    public List<UserPermission> findAllSysAdmin() {
        return repository.findByPermission(PermissionEnum.SYS_ADMIN);
    }

    @Override
    public List<UserPermission> findAllByUserApp(UserApp userApp) {
        return repository.findAllByUserApp(userApp);
    }

    private boolean hasNoPermission(PermissionEnum permissionEnum, Set<UserPermission> userPermissionList) {
        return userPermissionList
                .stream()
                .noneMatch(up -> up.getPermission().equals(permissionEnum));
    }

    private void verifyIfUserLoggedIsSuperUserAndArgumentIsSuperUser(UserApp userLogged, PermissionEnum permissionEnum) {
        boolean isNotSysAdmin = hasNoPermission(PermissionEnum.SYS_ADMIN, userLogged.getUserPermissionList());

        if (isNotSysAdmin && permissionEnum.equals(PermissionEnum.SYS_ADMIN)) {
            throw new ForbiddenException(
                    messagesService.get("user.without.permission.to.action"));
        }
    }

    private void verifyIfUserLoggedCanExecuteTheAction(UserApp userLogged, PermissionEnum permissionEnum) {
        verifyIfUserLoggedIsSuperUserAndArgumentIsSuperUser(userLogged, permissionEnum);

        boolean isNotSysAdmin = hasNoPermission(PermissionEnum.SYS_ADMIN, userLogged.getUserPermissionList());
        boolean isNotSpaceOwner = hasNoPermission(PermissionEnum.SPACE_OWNER, userLogged.getUserPermissionList());

        if (isNotSysAdmin && isNotSpaceOwner) {
            throw new ForbiddenException(
                    messagesService.get("user.without.permission.to.action"));
        }
    }
}
