package br.com.kbmg.wsmusiccontrol.controller;

import br.com.kbmg.wsmusiccontrol.config.security.SpringSecurityUtil;
import br.com.kbmg.wsmusiccontrol.config.security.UserCredentialsSecurity;
import br.com.kbmg.wsmusiccontrol.config.security.annotations.SecuredAnyUserAuth;
import br.com.kbmg.wsmusiccontrol.dto.user.UserDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserOnlyIdNameAndEmailDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserWithPermissionDto;
import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.repository.projection.UserOnlyIdNameAndEmailProjection;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import br.com.kbmg.wsmusiccontrol.service.UserPermissionService;
import br.com.kbmg.wsmusiccontrol.util.mapper.UserAppMapper;
import br.com.kbmg.wsmusiccontrol.util.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@SecuredAnyUserAuth
@Transactional
public class UserController extends GenericController {

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private UserPermissionService userPermissionService;

    @Autowired
    private UserAppMapper userAppMapper;

    @GetMapping("")
    public ResponseEntity<ResponseData<Set<UserWithPermissionDto>>> findAllBySpace() {
        String spaceId = SpringSecurityUtil.getCredentials().getSpaceId();
        Set<UserWithPermissionDto> entityData = userAppService.findAllBySpace(spaceId);
        return super.ok(entityData);
    }

    @PutMapping("/logged")
    public ResponseEntity<ResponseData<UserWithPermissionDto>> updateUserLogged(
            @Valid @RequestBody UserDto body
    ) {
        UserCredentialsSecurity credentials = SpringSecurityUtil.getCredentials();
        UserApp entityData = userAppService.updateUserLogged(body);
        UserWithPermissionDto viewData = userAppMapper.toUserWithPermissionDto(entityData);
        viewData.setPermissionList(credentials.getRoles().stream().map(PermissionEnum::valueOf).collect(Collectors.toSet()));
        return super.ok(viewData);
    }

    @GetMapping("/logged")
    public ResponseEntity<ResponseData<UserWithPermissionDto>> findUserLogged() {
        UserApp entityData = userAppService.findUserLogged();
        UserWithPermissionDto viewData = userAppMapper.toUserWithPermissionDto(entityData);
        viewData.setPermissionList(SpringSecurityUtil.getCredentials().getRoles().stream().map(PermissionEnum::valueOf).collect(Collectors.toSet()));
        return super.ok(viewData);
    }

    @GetMapping("/association-for-events")
    public ResponseEntity<ResponseData<List<UserOnlyIdNameAndEmailDto>>> findUsersAssociationForEventsBySpace() {
        String spaceId = SpringSecurityUtil.getCredentials().getSpaceId();
        List<UserOnlyIdNameAndEmailProjection> projectionList = userAppService.findUsersAssociationForEventsBySpace(spaceId);
        List<UserOnlyIdNameAndEmailDto> viewData = userAppMapper.toUserOnlyIdNameAndEmailDto(projectionList);
        return super.ok(viewData);
    }

    @PostMapping("/{email-user}/permissions/{permission-key}")
    public ResponseEntity<ResponseData<Void>> addPermission(
            @PathVariable("email-user") String emailUser,
            @PathVariable("permission-key") PermissionEnum permissionEnum
            ) {
        String spaceId = SpringSecurityUtil.getCredentials().getSpaceId();
        userAppService.addPermissionToUserInSpace(emailUser, spaceId, permissionEnum);
        return super.ok();
    }

}
