package br.com.kbmg.wshammeron.controller;

import br.com.kbmg.wshammeron.config.recaptcha.v3.AbstractCaptchaService;
import br.com.kbmg.wshammeron.config.recaptcha.v3.RecaptchaEnum;
import br.com.kbmg.wshammeron.dto.auth.JwtTokenDto;
import br.com.kbmg.wshammeron.dto.user.ActivateUserAccountRefreshDto;
import br.com.kbmg.wshammeron.dto.user.LoginDto;
import br.com.kbmg.wshammeron.dto.user.RegisterDto;
import br.com.kbmg.wshammeron.dto.user.RegisterPasswordDto;
import br.com.kbmg.wshammeron.dto.user.UserChangePasswordDto;
import br.com.kbmg.wshammeron.dto.user.UserTokenHashDto;
import br.com.kbmg.wshammeron.service.JwtService;
import br.com.kbmg.wshammeron.service.SecurityService;
import br.com.kbmg.wshammeron.service.UserAppService;
import br.com.kbmg.wshammeron.util.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping("/security")
@CrossOrigin(origins = "*")
public class SecurityController extends GenericController {

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AbstractCaptchaService recaptchaService;


    @PostMapping("/token-login")
    @Transactional
    public ResponseEntity<ResponseData<JwtTokenDto>> loginAndGetToken(
            @RequestBody @Valid LoginDto loginDto, @RequestParam(name="g-recaptcha-response") String recaptchaResponse) {
        recaptchaService.processResponse(recaptchaResponse, RecaptchaEnum.LOGIN_ACTION.getValue());
        String token = securityService.validateLoginAndGetToken(loginDto);

        return super.ok(new JwtTokenDto(token));
    }

    @PostMapping("/password-recovery")
    public ResponseEntity<ResponseData<Void>> passwordRecovery(
            @RequestBody @Valid ActivateUserAccountRefreshDto activateUserAccountRefreshDto,
            HttpServletRequest request) {

        securityService.passwordRecovery(activateUserAccountRefreshDto, request);

        return super.ok();
    }

    @PostMapping("/password-recovery/change")
    public ResponseEntity<ResponseData<Void>> passwordRecoveryChange(
            @RequestBody @Valid UserChangePasswordDto userChangePasswordDto) {

        securityService.passwordRecoveryChange(userChangePasswordDto);

        return super.ok();
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseData<Void>> registerUserAccount(
            @RequestBody @Valid RegisterDto registerDto,
            HttpServletRequest request) {

        securityService.registerNewUserAccount(registerDto, request);

        return super.ok();
    }

    @PostMapping("/register/password")
    public ResponseEntity<ResponseData<Void>> registerUserPassword(
            @RequestBody @Valid RegisterPasswordDto registerPasswordDto) {

        userAppService.registerUserPassword(registerPasswordDto);

        return super.ok();
    }

    @PostMapping("/register/token")
    @Transactional
    public ResponseEntity<ResponseData<Void>> activateUserAccount(
            @RequestBody @Valid UserTokenHashDto userTokenHashDto) {

        securityService.activateUserAccount(userTokenHashDto);

        return super.ok();
    }

    @PostMapping("/register/token/refresh")
    public ResponseEntity<ResponseData<Void>> resendMailToken(
            @RequestBody @Valid ActivateUserAccountRefreshDto activateUserAccountRefreshDto,
            HttpServletRequest request) {

        securityService.resendMailToken(activateUserAccountRefreshDto, request);

        return super.ok();
    }

}
