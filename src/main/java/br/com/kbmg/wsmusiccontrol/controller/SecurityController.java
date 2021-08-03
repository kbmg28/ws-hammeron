package br.com.kbmg.wsmusiccontrol.controller;

import br.com.kbmg.wsmusiccontrol.dto.LoginDto;
import br.com.kbmg.wsmusiccontrol.dto.TokenDto;
import br.com.kbmg.wsmusiccontrol.dto.ActivateUserAccountRefreshDto;
import br.com.kbmg.wsmusiccontrol.dto.UserDto;
import br.com.kbmg.wsmusiccontrol.dto.UserTokenDto;
import br.com.kbmg.wsmusiccontrol.service.JwtService;
import br.com.kbmg.wsmusiccontrol.service.SecurityService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import br.com.kbmg.wsmusiccontrol.util.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/security")
@CrossOrigin(origins = "*")
public class SecurityController {

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/token-login")
    public ResponseEntity<ResponseData<TokenDto>> loginAndGetToken(
            @RequestBody @Valid LoginDto loginDto) {

        String token = securityService.validateLoginAndGetToken(loginDto);

        return ResponseEntity.ok(new ResponseData<>(new TokenDto(token)));
    }

    @PostMapping("/token-activate/refresh")
    public ResponseEntity<ResponseData<Void>> resendMailToken(
            @RequestBody @Valid ActivateUserAccountRefreshDto activateUserAccountRefreshDto,
            HttpServletRequest request) {

        securityService.resendMailToken(activateUserAccountRefreshDto, request);

        return ResponseEntity.ok(new ResponseData<>());
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseData<String>> registerUserAccount(
            @RequestBody @Valid UserDto userDto,
            HttpServletRequest request) {
        securityService.registerNewUserAccount(userDto, request);
        return ResponseEntity.ok(new ResponseData<>("ok", null));
    }

    @PostMapping("/activate")
    public ResponseEntity<ResponseData<Void>> activateUserAccount(
            @RequestBody @Valid UserTokenDto userTokenDto) {
        securityService.activateUserAccount(userTokenDto);
        return ResponseEntity.ok(new ResponseData<>());
    }

}
