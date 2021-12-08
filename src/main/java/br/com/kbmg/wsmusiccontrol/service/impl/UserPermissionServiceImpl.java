package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.config.security.SpringSecurityUtil;
import br.com.kbmg.wsmusiccontrol.constants.AppConstants;
import br.com.kbmg.wsmusiccontrol.dto.user.UserWithPermissionDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserWithSinglePermissionDto;
import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.exception.ForbiddenException;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.SpaceUserAppAssociation;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.UserPermission;
import br.com.kbmg.wsmusiccontrol.repository.UserPermissionRepository;
import br.com.kbmg.wsmusiccontrol.service.SpaceService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import br.com.kbmg.wsmusiccontrol.service.UserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserPermissionServiceImpl
        extends GenericServiceImpl<UserPermission, UserPermissionRepository>
        implements UserPermissionService {

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private SpaceService spaceService;

    @Override
    public void addPermissionToUser(SpaceUserAppAssociation spaceUserAppAssociation, PermissionEnum permissionEnum) {
        Set<UserPermission> userPermissionList = spaceUserAppAssociation.getUserPermissionList();
        Set<String> allPermissionsOfUser = userPermissionList
                .stream()
                .map(userPermission -> userPermission.getPermission().toString())
                .collect(Collectors.toSet());

        if(spaceUserAppAssociation.getUserApp().getIsSysAdmin()) {
            allPermissionsOfUser.add(AppConstants.SYS_ADMIN);
        }

        boolean hasNoPermission = hasNoPermission(permissionEnum.toString(), allPermissionsOfUser);

        if(hasNoPermission) {
            UserApp userLogged = userAppService.findUserLogged();

            if (userLogged != null) {
                verifyIfUserLoggedCanExecuteTheAction(userLogged, permissionEnum.toString());
            }

            UserPermission newUserPermission = new UserPermission();
            newUserPermission.setPermission(permissionEnum);
            newUserPermission.setSpaceUserAppAssociation(spaceUserAppAssociation);

            repository.save(newUserPermission);

            userPermissionList.add(newUserPermission);
        } else {
            String errorMessage = messagesService.get("space.user.already.exists");
            throw new ServiceException(errorMessage);
        }
    }

    @Override
    public List<String> findAllBySpaceAndUserApp(Space space, UserApp userApp) {
        List<String> userPermissionList =
                repository
                    .findAllByUserAppAndSpace(userApp, space)
                    .stream()
                    .map(PermissionEnum::name)
                    .collect(Collectors.toList());

        if(userApp.getIsSysAdmin()) {
            userPermissionList.add(AppConstants.SYS_ADMIN);
        }

        return userPermissionList;
    }

    @Override
    public void checkPermissionsOfUsers(Space space, Set<UserWithPermissionDto> viewData) {
        Set<String> emails = viewData.stream().map(UserWithPermissionDto::getEmail).collect(Collectors.toSet());
        List<UserWithSinglePermissionDto> dto = repository.findBySpaceAndEmailList(space, emails);

        Map<String, Set<PermissionEnum>> permissionsByUserMap = dto.stream().collect(
                Collectors.groupingBy(UserWithSinglePermissionDto::getEmail,
                        HashMap::new,
                        Collectors.mapping(UserWithSinglePermissionDto::getPermission,
                                Collectors.toSet())));
        viewData.forEach(user -> {
            Set<PermissionEnum> permissions = permissionsByUserMap.get(user.getEmail());
            user.setPermissionList(permissions);
        });
    }

    private boolean hasNoPermission(String permission, Set<String> allPermissions) {
        return allPermissions
                .stream()
                .noneMatch(role -> role.equals(permission));
    }

    private void verifyIfUserLoggedIsSuperUserAndArgumentIsSuperUser(String permission, Set<String> allPermissions) {
        boolean isNotSysAdmin = hasNoPermission(AppConstants.SYS_ADMIN, allPermissions);

        if (isNotSysAdmin && permission.equals(AppConstants.SYS_ADMIN)) {
            throw new ForbiddenException(
                    messagesService.get("user.without.permission.to.action"));
        }
    }

    private void verifyIfUserLoggedCanExecuteTheAction(UserApp userLogged, String permission) {
        Set<String> allPermissions = SpringSecurityUtil.getAllPermissions();

        verifyIfUserLoggedIsSuperUserAndArgumentIsSuperUser(permission, allPermissions);

        boolean isNotSysAdmin = hasNoPermission(AppConstants.SYS_ADMIN, allPermissions);
        boolean isNotSpaceOwner = hasNoPermission(PermissionEnum.SPACE_OWNER.toString(), allPermissions);

        if (isNotSysAdmin && isNotSpaceOwner) {
            throw new ForbiddenException(
                    messagesService.get("user.without.permission.to.action"));
        }
    }
}
