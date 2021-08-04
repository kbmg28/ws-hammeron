package br.com.kbmg.wsmusiccontrol.config.security;

import br.com.kbmg.wsmusiccontrol.config.messages.MessagesService;
import br.com.kbmg.wsmusiccontrol.exception.AuthorizationException;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.service.JwtService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static br.com.kbmg.wsmusiccontrol.constants.JwtConstants.BEARER;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.AUTHORIZATION_REQUIRED;

@Slf4j
public class ValidateJwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserSpringSecurityService userSpringSecurityService;

    @Autowired
    private UserAppService userAppService;

    @Autowired
    public MessagesService messagesService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
        throws ServletException, IOException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (isRequestForApi(request)) {
            try {
                UserApp userApp = this.authJwtTokenAndGetUser(authorization);
                this.loadUserSpringSecurity(userApp, request);

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                if (e instanceof AuthorizationException) {
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRequestForApi(HttpServletRequest request) {
        return request.getRequestURL().indexOf("/api/") >= 0;
    }

    private void loadUserSpringSecurity(UserApp userApp, HttpServletRequest request) {
        userSpringSecurityService.loadSpringSecurityInContext(userApp, request);
    }

    private UserApp authJwtTokenAndGetUser(String authorization) {
        if (Strings.isEmpty(authorization) || !authorization.startsWith(BEARER)) {
            throw new AuthorizationException(messagesService.get(AUTHORIZATION_REQUIRED));
        }

        String jwtToken = authorization.substring(7, authorization.length());

        Long userId = jwtService.validateTokenAndGetUserId(jwtToken);

        return userAppService.findById(userId).orElseThrow(AuthorizationException::new);
    }

}
