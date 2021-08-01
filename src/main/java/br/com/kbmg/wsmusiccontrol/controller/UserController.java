package br.com.kbmg.wsmusiccontrol.controller;

import br.com.kbmg.wsmusiccontrol.dto.UserDto;
import br.com.kbmg.wsmusiccontrol.event.OnRegistrationCompleteEvent;
import br.com.kbmg.wsmusiccontrol.model.User;
import br.com.kbmg.wsmusiccontrol.service.UserService;
import br.com.kbmg.wsmusiccontrol.util.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    private UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<ResponseData<String>> registerUserAccount(
            @RequestBody @Validated UserDto userDto,
            HttpServletRequest request) {

        User registered = userService.registerNewUserAccount(userDto);

        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
                request.getLocale(), appUrl));
        return ResponseEntity.ok(new ResponseData<>("ok", null));
    }

}
