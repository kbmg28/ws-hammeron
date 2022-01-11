package br.com.kbmg.wsmusiccontrol.controller;

import br.com.kbmg.wsmusiccontrol.config.security.SpringSecurityUtil;
import br.com.kbmg.wsmusiccontrol.config.security.annotations.SecuredAnyUserAuth;
import br.com.kbmg.wsmusiccontrol.dto.user.UserDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserOnlyIdNameAndEmailDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserWithPermissionDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserWithoutPermissionDto;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

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
    public ResponseEntity<ResponseData<UserWithoutPermissionDto>> updateUserLogged(
            @Valid @RequestBody UserDto body
    ) {
        UserApp entityData = userAppService.updateUserLogged(body);
        return super.ok(new UserWithoutPermissionDto(entityData));
    }

    @GetMapping("/logged")
    public ResponseEntity<ResponseData<UserWithoutPermissionDto>> findUserLogged() {
        UserApp entityData = userAppService.findUserLogged();
        return super.ok(new UserWithoutPermissionDto(entityData));
    }

    @GetMapping("/association-for-events")
    public ResponseEntity<ResponseData<List<UserOnlyIdNameAndEmailDto>>> findUsersAssociationForEventsBySpace() {
        String spaceId = SpringSecurityUtil.getCredentials().getSpaceId();
        List<UserOnlyIdNameAndEmailProjection> projectionList = userAppService.findUsersAssociationForEventsBySpace(spaceId);
        List<UserOnlyIdNameAndEmailDto> viewData = userAppMapper.toUserOnlyIdNameAndEmailDto(projectionList);
        return super.ok(viewData);
    }

    @PutMapping("/{email-user}/permissions/{permission-key}")
    public ResponseEntity<ResponseData<Void>> updatePermission(
            @PathVariable("email-user") String emailUser,
            @PathVariable("permission-key") PermissionEnum permissionEnum
            ) {
        String spaceId = SpringSecurityUtil.getCredentials().getSpaceId();
        userAppService.addPermissionToUserInSpace(emailUser, spaceId, permissionEnum);
        return super.ok();
    }

}
