package br.com.kbmg.wshammeron.service.impl;

import br.com.kbmg.wshammeron.config.security.SpringSecurityUtil;
import br.com.kbmg.wshammeron.constants.AppConstants;
import br.com.kbmg.wshammeron.dto.user.UserWithPermissionDto;
import br.com.kbmg.wshammeron.dto.user.UserWithSinglePermissionDto;
import br.com.kbmg.wshammeron.enums.PermissionEnum;
import br.com.kbmg.wshammeron.exception.ForbiddenException;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.SpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.model.UserPermission;
import br.com.kbmg.wshammeron.repository.UserPermissionRepository;
import br.com.kbmg.wshammeron.service.SpaceService;
import br.com.kbmg.wshammeron.service.UserAppService;
import br.com.kbmg.wshammeron.service.UserPermissionService;
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

        verifyIfUserLoggedCanExecuteTheAction(permissionEnum.toString());

        UserPermission userPermission = userPermissionList.stream().findFirst().orElseGet(() -> {
            UserPermission newUserPermission = new UserPermission();
            newUserPermission.setSpaceUserAppAssociation(spaceUserAppAssociation);

            userPermissionList.add(newUserPermission);
            return newUserPermission;
        });

        userPermission.setPermission(permissionEnum);
        repository.save(userPermission);
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

    private void verifyIfUserLoggedCanExecuteTheAction(String permission) {
        UserApp userLogged = userAppService.findUserLogged();

        if (userLogged != null) {
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
}
