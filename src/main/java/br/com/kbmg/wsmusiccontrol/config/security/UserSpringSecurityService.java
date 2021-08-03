package br.com.kbmg.wsmusiccontrol.config.security;

import br.com.kbmg.wsmusiccontrol.model.UserApp;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;

public interface UserSpringSecurityService extends UserDetailsService {

    UserCredentialsSecurity loadSpringSecurityInContext(UserApp userApp, HttpServletRequest request);
    UserDetails loadUser(UserApp userApp);
}
