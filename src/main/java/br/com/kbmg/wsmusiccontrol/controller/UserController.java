package br.com.kbmg.wsmusiccontrol.controller;

import br.com.kbmg.wsmusiccontrol.config.security.SecuredAdmin;
import br.com.kbmg.wsmusiccontrol.config.security.SecuredAdminOrUser;
import br.com.kbmg.wsmusiccontrol.dto.user.UserWithPermissionDto;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import br.com.kbmg.wsmusiccontrol.util.mapper.UserAppMapper;
import br.com.kbmg.wsmusiccontrol.util.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
@SecuredAdminOrUser
public class UserController extends GenericController {

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private UserAppMapper userAppMapper;

    @GetMapping("/all")
    @Transactional
    @SecuredAdmin
    public ResponseEntity<ResponseData<Set<UserWithPermissionDto>>> findAll() {
        List<UserApp> entityData = userAppService.findAll();
        Set<UserWithPermissionDto> viewData = userAppMapper.toUserWithPermissionDtoList(entityData);
        return super.ok(viewData);
    }

}
