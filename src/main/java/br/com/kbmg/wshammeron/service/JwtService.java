package br.com.kbmg.wshammeron.service;

import br.com.kbmg.wshammeron.dto.user.LoginDto;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.UserApp;

import javax.servlet.http.HttpServletRequest;

public interface JwtService {

    String generateToken(LoginDto loginDto, UserApp userApp, Space lastAccessedSpace);
    boolean isValidToken(String token);
    String validateTokenAndGetUserId(String token);

    String getValue(String token, String key);

    String updateSpaceOnToken(HttpServletRequest request, Space space);
}
