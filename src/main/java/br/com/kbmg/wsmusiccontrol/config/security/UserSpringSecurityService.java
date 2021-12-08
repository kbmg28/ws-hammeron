package br.com.kbmg.wsmusiccontrol.config.security;

import br.com.kbmg.wsmusiccontrol.dto.auth.AuthInfoDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;

public interface UserSpringSecurityService extends UserDetailsService {

    UserCredentialsSecurity loadSpringSecurityInContext(AuthInfoDto authInfoDto, HttpServletRequest request);
}
