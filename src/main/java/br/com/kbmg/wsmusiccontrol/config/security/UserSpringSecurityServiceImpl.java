package br.com.kbmg.wsmusiccontrol.config.security;

import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.model.User;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@Slf4j
public class UserSpringSecurityServiceImpl implements UserSpringSecurityService {

    private static final Gson gson = new Gson();

    @Override
    public UserDetails loadUserByUsername(String email) {

        br.com.kbmg.wsmusiccontrol.model.User user = new User();

        // TODO: implement get user by email

        return loadUser(user);
    }

    @Override
    public UserDetails loadUser(br.com.kbmg.wsmusiccontrol.model.User user) {
        return this.generateUserDetails(user.getEmail(), this.loadPermissions(user));
    }

    @Override
    public UserCredentialsSecurity loadSpringSecurityInContext(UserDetails userDetails, HttpServletRequest request) {
        UserCredentialsSecurity userCredentialsSecurity = new UserCredentialsSecurity(userDetails, request);

        UsernamePasswordAuthenticationToken userToken =
            new UsernamePasswordAuthenticationToken(userDetails, userCredentialsSecurity, userDetails.getAuthorities());

        userToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(userToken);

        return userCredentialsSecurity;
    }

    private UserDetails generateUserDetails(String email, String[] roles) {
        // TODO: USE --> return org.springframework.security.core.userdetails.User.builder().username(email).roles(roles).password("").build();
        return org.springframework.security.core.userdetails.User.builder().username("test@test.com").roles(PermissionEnum.ADMIN.toString()).password("").build();
    }

    private String[] loadPermissions(br.com.kbmg.wsmusiccontrol.model.User user) {
        return user
                .getUserPermissionList()
                .stream()
                .map(userPermission ->
                        userPermission.getPermission().toString())
                .distinct()
                .toArray(String[]::new);
    }

}
