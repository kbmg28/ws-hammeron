package br.com.kbmg.wsmusiccontrol.config.security;

import br.com.kbmg.wsmusiccontrol.config.messages.MessagesService;
import br.com.kbmg.wsmusiccontrol.dto.auth.AuthInfoDto;
import br.com.kbmg.wsmusiccontrol.exception.AuthorizationException;
import br.com.kbmg.wsmusiccontrol.exception.ForbiddenException;
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
import static br.com.kbmg.wsmusiccontrol.constants.JwtConstants.CLAIM_SPACE_ID;
import static br.com.kbmg.wsmusiccontrol.constants.JwtConstants.CLAIM_SPACE_NAME;
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


        if (isRequestForApi(request)) {
            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

            try {
                AuthInfoDto authInfoDto = this.authJwtTokenAndGetUser(authorization);
                this.loadUserSpringSecurity(authInfoDto, request);

            } catch (Exception e) {
                if (e instanceof AuthorizationException) {
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
                    return;
                } else if (e instanceof ForbiddenException) {
                    response.sendError(HttpStatus.FORBIDDEN.value(), e.getMessage());
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRequestForApi(HttpServletRequest request) {
        return request.getRequestURL().indexOf("/api/") >= 0;
    }

    private void loadUserSpringSecurity(AuthInfoDto authInfoDto, HttpServletRequest request) {
        userSpringSecurityService.loadSpringSecurityInContext(authInfoDto, request);
    }

    private AuthInfoDto authJwtTokenAndGetUser(String authorization) {
        if (Strings.isBlank(authorization) || !authorization.startsWith(BEARER)) {
            throw new AuthorizationException(null, messagesService.get(AUTHORIZATION_REQUIRED));
        }

        String jwtToken = authorization.substring(7, authorization.length());

        String userId = jwtService.validateTokenAndGetUserId(jwtToken);
        String spaceId = jwtService.getValue(jwtToken, CLAIM_SPACE_ID);
        String spaceName = jwtService.getValue(jwtToken, CLAIM_SPACE_NAME);
        UserApp userApp = userAppService.findById(userId).orElseThrow(AuthorizationException::new);

        return new AuthInfoDto(userApp, spaceId, spaceName);
    }

}
