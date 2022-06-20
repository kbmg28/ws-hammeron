package br.com.kbmg.wshammeron.service;

import br.com.kbmg.wshammeron.dto.user.ActivateUserAccountRefreshDto;
import br.com.kbmg.wshammeron.dto.user.LoginDto;
import br.com.kbmg.wshammeron.dto.user.RegisterDto;
import br.com.kbmg.wshammeron.dto.user.UserChangePasswordDto;
import br.com.kbmg.wshammeron.dto.user.UserTokenHashDto;
import br.com.kbmg.wshammeron.model.UserApp;

import javax.servlet.http.HttpServletRequest;

public interface SecurityService {

    String validateLoginAndGetToken(LoginDto loginDto);

    void registerNewUserAccount(RegisterDto userDto, HttpServletRequest request);

    void activateUserAccount(UserTokenHashDto userTokenHashDto);

    void createVerificationToken(UserApp userApp, String token);

    void resendMailToken(ActivateUserAccountRefreshDto activateUserAccountRefreshDto, HttpServletRequest request);

    void passwordRecovery(ActivateUserAccountRefreshDto activateUserAccountRefreshDto, HttpServletRequest request);

    void passwordRecoveryChange(UserChangePasswordDto userChangePasswordDto);
}
