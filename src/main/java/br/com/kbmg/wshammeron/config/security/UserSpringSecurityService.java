package br.com.kbmg.wshammeron.config.security;

import br.com.kbmg.wshammeron.dto.auth.AuthInfoDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;

public interface UserSpringSecurityService extends UserDetailsService {

    UserCredentialsSecurity loadSpringSecurityInContext(AuthInfoDto authInfoDto, HttpServletRequest request);
}
