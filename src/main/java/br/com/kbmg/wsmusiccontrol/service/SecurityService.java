package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.user.ActivateUserAccountRefreshDto;
import br.com.kbmg.wsmusiccontrol.dto.user.LoginDto;
import br.com.kbmg.wsmusiccontrol.dto.user.RegisterDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserTokenHashDto;
import br.com.kbmg.wsmusiccontrol.model.UserApp;

import javax.servlet.http.HttpServletRequest;

public interface SecurityService {

    String validateLoginAndGetToken(LoginDto loginDto);

    void registerNewUserAccount(RegisterDto userDto, HttpServletRequest request);

    void activateUserAccount(UserTokenHashDto userTokenHashDto);

    void createVerificationToken(UserApp userApp, String token);

    void resendMailToken(ActivateUserAccountRefreshDto activateUserAccountRefreshDto, HttpServletRequest request);
}
