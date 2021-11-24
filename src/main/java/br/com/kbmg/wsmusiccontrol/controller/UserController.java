package br.com.kbmg.wsmusiccontrol.controller;

import br.com.kbmg.wsmusiccontrol.config.security.annotations.SecuredAnyUserAuth;
import br.com.kbmg.wsmusiccontrol.dto.user.UserDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserOnlyIdNameAndEmailDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserWithPermissionDto;
import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.UserPermission;
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

@RestController
@RequestMapping("/api/spaces/{space-id}/users")
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
    public ResponseEntity<ResponseData<Set<UserWithPermissionDto>>> findAllBySpace(
            @PathVariable("space-id") String spaceId
    ) {
        List<UserApp> entityData = userAppService.findAllBySpace(spaceId);
        Set<UserWithPermissionDto> viewData = userAppMapper.toUserWithPermissionDtoList(entityData);
        return super.ok(viewData);
    }

    @PutMapping("/logged")
    public ResponseEntity<ResponseData<UserWithPermissionDto>> updateUserLogged(
            @PathVariable("space-id") String spaceId,
            @Valid @RequestBody UserDto body
    ) {
        UserApp entityData = userAppService.updateUserLogged(spaceId, body);
        UserWithPermissionDto viewData = userAppMapper.toUserWithPermissionDto(entityData);
        return super.ok(viewData);
    }

    @GetMapping("/logged")
    public ResponseEntity<ResponseData<UserWithPermissionDto>> findUserLogged(
            @PathVariable("space-id") String spaceId
    ) {
        UserApp entityData = userAppService.findUserLogged();
        UserWithPermissionDto viewData = userAppMapper.toUserWithPermissionDto(entityData);
        return super.ok(viewData);
    }

    @GetMapping("/association-for-events")
    public ResponseEntity<ResponseData<List<UserOnlyIdNameAndEmailDto>>> findUsersAssociationForEventsBySpace(
            @PathVariable("space-id") String spaceId
    ) {
        List<UserOnlyIdNameAndEmailProjection> projectionList = userAppService.findUsersAssociationForEventsBySpace(spaceId);
        List<UserOnlyIdNameAndEmailDto> viewData = userAppMapper.toUserOnlyIdNameAndEmailDto(projectionList);
        return super.ok(viewData);
    }

    @GetMapping("/permissions/{permission-key}")
    public ResponseEntity<ResponseData<Set<UserDto>>> findAllByPermission(
            @PathVariable("space-id") String spaceId,
            @PathVariable("permission-key") PermissionEnum permissionEnum
            ) {
        List<UserPermission> entityData = userPermissionService.findBySpaceAndPermission(spaceId, permissionEnum);
        Set<UserDto> viewData = userAppMapper.toUserDtoFromUserPermissionList(entityData);
        return super.ok(viewData);
    }

    @PostMapping("/{email-user}/permissions/{permission-key}")
    public ResponseEntity<ResponseData<Void>> addPermission(
            @PathVariable("space-id") String spaceId,
            @PathVariable("email-user") String emailUser,
            @PathVariable("permission-key") PermissionEnum permissionEnum
            ) {
        userAppService.addPermissionToUserInSpace(emailUser, spaceId, permissionEnum);
        return super.ok();
    }

}
