package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.ActivateUserAccountRefreshDto;
import br.com.kbmg.wsmusiccontrol.dto.LoginDto;
import br.com.kbmg.wsmusiccontrol.dto.UserDto;
import br.com.kbmg.wsmusiccontrol.dto.UserTokenHashDto;
import br.com.kbmg.wsmusiccontrol.model.UserApp;

import javax.servlet.http.HttpServletRequest;

public interface SecurityService {

    String validateLoginAndGetToken(LoginDto loginDto);

    void registerNewUserAccount(UserDto userDto, HttpServletRequest request);

    void activateUserAccount(UserTokenHashDto userTokenHashDto);

    void createVerificationToken(UserApp userApp, String token);

    void resendMailToken(ActivateUserAccountRefreshDto activateUserAccountRefreshDto, HttpServletRequest request);
}
