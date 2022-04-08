package br.com.kbmg.wshammeron.config.security;

import br.com.kbmg.wshammeron.dto.auth.AuthInfoDto;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.service.SpaceService;
import br.com.kbmg.wshammeron.service.UserPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@Slf4j
public class UserSpringSecurityServiceImpl implements UserSpringSecurityService {

    @Autowired
    private UserPermissionService userPermissionService;

    @Autowired
    private SpaceService spaceService;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return null;
    }

    @Override
    public UserCredentialsSecurity loadSpringSecurityInContext(AuthInfoDto authInfoDto, HttpServletRequest request) {
        UserDetails userDetails = this.loadUser(authInfoDto);
        UserCredentialsSecurity userCredentialsSecurity = new UserCredentialsSecurity(authInfoDto.getSpaceId(), authInfoDto.getSpaceName(), userDetails, request);

        UsernamePasswordAuthenticationToken userToken =
            new UsernamePasswordAuthenticationToken(userDetails, userCredentialsSecurity, userDetails.getAuthorities());

        userToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(userToken);

        return userCredentialsSecurity;
    }

    private UserDetails generateUserDetails(String email, String[] roles) {
        return org.springframework.security.core.userdetails.User.builder().username(email).roles(roles).password("").build();
    }

    private UserDetails loadUser(AuthInfoDto authInfoDto) {
        UserApp userApp = authInfoDto.getUserApp();
        Space space = spaceService.findByIdAndUserAppValidated(authInfoDto.getSpaceId(), userApp);
        List<String> userPermissionList = userPermissionService.findAllBySpaceAndUserApp(space, userApp);

        return this.generateUserDetails(userApp.getEmail(), userPermissionList.toArray(String[]::new));
    }

}
