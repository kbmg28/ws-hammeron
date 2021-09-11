package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.user.LoginDto;
import br.com.kbmg.wsmusiccontrol.model.UserApp;

public interface JwtService {

    String generateToken(LoginDto loginDto, UserApp userApp);
    boolean isValidToken(String token);
    Long validateTokenAndGetUserId(String token);
}
