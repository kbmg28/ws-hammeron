package br.com.kbmg.wsmusiccontrol.config.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;

public interface UserSpringSecurityService extends UserDetailsService {

    UserCredentialsSecurity loadSpringSecurityInContext(UserDetails userDetails, HttpServletRequest request);
    UserDetails loadUser(br.com.kbmg.wsmusiccontrol.model.User user);
}
