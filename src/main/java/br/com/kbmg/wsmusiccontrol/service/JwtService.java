package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.user.LoginDto;
import br.com.kbmg.wsmusiccontrol.model.UserApp;

import java.util.UUID;

public interface JwtService {

    String generateToken(LoginDto loginDto, UserApp userApp);
    boolean isValidToken(String token);
    String validateTokenAndGetUserId(String token);
}
