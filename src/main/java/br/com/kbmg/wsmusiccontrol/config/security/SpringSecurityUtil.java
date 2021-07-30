package br.com.kbmg.wsmusiccontrol.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class SpringSecurityUtil {

    public static UserCredentialsSecurity getCredentials() {
        UserCredentialsSecurity credentials;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            credentials = (UserCredentialsSecurity) authentication.getCredentials();

        } catch (Exception e) {
            credentials = null;
        }

        return credentials;
    }

    public static String getEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static Set<String> getAllPermissions() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(SpringSecurityUtil::parseAuthorityToString)
                .collect(Collectors.toSet());
    }

    /**
     * GrantedAuthority converter in string representation enum. <br>
     * <br>
     * Example: 'ROLE_ADMIN' --> 'ADMIN'
     *
     * @param grantedAuthority the role
     * @return the string representation
     */
    private static String parseAuthorityToString(GrantedAuthority grantedAuthority) {
        String s = String.valueOf(grantedAuthority);
        int i = s.indexOf('_') + 1;
        int length = s.length();

        return s.substring(i, length);
    }

}
