package br.com.kbmg.wsmusiccontrol.config.security;

import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.UserPermission;
import br.com.kbmg.wsmusiccontrol.service.UserPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Slf4j
public class UserSpringSecurityServiceImpl implements UserSpringSecurityService {

    @Autowired
    private UserPermissionService userPermissionService;

    @Override
    public UserDetails loadUserByUsername(String email) {
        UserApp userApp = new UserApp();
        return loadUser(userApp);
    }

    @Override
    @Transactional
    public UserDetails loadUser(UserApp userApp) {
        return this.generateUserDetails(userApp.getEmail(), this.loadPermissions(userApp));
    }

    @Override
    public UserCredentialsSecurity loadSpringSecurityInContext(UserApp userApp, HttpServletRequest request) {
        UserDetails userDetails = this.loadUser(userApp);
        UserCredentialsSecurity userCredentialsSecurity = new UserCredentialsSecurity(userDetails, request);

        UsernamePasswordAuthenticationToken userToken =
            new UsernamePasswordAuthenticationToken(userDetails, userCredentialsSecurity, userDetails.getAuthorities());

        userToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(userToken);

        return userCredentialsSecurity;
    }

    private UserDetails generateUserDetails(String email, String[] roles) {
        return org.springframework.security.core.userdetails.User.builder().username(email).roles(roles).password("").build();
    }

    private String[] loadPermissions(UserApp userApp) {
        List<UserPermission> permissionList = userPermissionService.findAllByUserApp(userApp);
        return permissionList
                .stream()
                .map(userPermission ->
                        userPermission.getPermission().toString())
                .distinct()
                .toArray(String[]::new);
    }

}
