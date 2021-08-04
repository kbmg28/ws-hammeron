package br.com.kbmg.wsmusiccontrol.controller;

import br.com.kbmg.wsmusiccontrol.config.security.SecuredAdminOrUser;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import br.com.kbmg.wsmusiccontrol.util.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
@SecuredAdminOrUser
public class UserController extends GenericController {

    @Autowired
    private UserAppService userAppService;

    @GetMapping("/all")
    @Transactional
    public ResponseEntity<ResponseData<List<String>>> findAll() {

        List<String> collect = userAppService.findAll().stream().map(user -> user.getName() + " " + user.getEmail() + " " +
                user.getUserPermissionList().stream().map(up -> up.getPermission().toString()).collect(Collectors.toList()))
                .collect(Collectors.toList());

        return super.ok(collect);
    }

}
