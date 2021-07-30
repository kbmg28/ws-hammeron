package br.com.kbmg.wsmusiccontrol.config.security;

import br.com.kbmg.wsmusiccontrol.exception.AuthorizationException;
import br.com.kbmg.wsmusiccontrol.model.User;
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

@Slf4j
public class ValidateJwtTokenFilter extends OncePerRequestFilter {

//    @Autowired
//    private SecurityService securityService;

    @Autowired
    private UserSpringSecurityService userSpringSecurityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
        throws ServletException, IOException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (isRequestForApi(request)) {
            try {
                User user = this.authJwtTokenAndGetUser(authorization);
                this.loadUserSpringSecurity(user, request);

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

    private void loadUserSpringSecurity(User user, HttpServletRequest request) {
        userSpringSecurityService.loadSpringSecurityInContext(userSpringSecurityService.loadUser(user), request);
    }

    private User authJwtTokenAndGetUser(String authorization) {
        if (Strings.isEmpty(authorization)) {
            throw new AuthorizationException("Authorization required");
        }

        // TODO:
        //  implement and return user - securityService.verifyJwtToken(authorization)

        return new User();
    }

}