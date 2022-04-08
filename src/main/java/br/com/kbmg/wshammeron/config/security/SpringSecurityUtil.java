package br.com.kbmg.wshammeron.config.security;

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
        UserCredentialsSecurity credentials = getCredentials();
        return  (credentials != null) ? credentials.getEmail() : null;
    }

    public static String getSpaceId() {
        UserCredentialsSecurity credentials = getCredentials();
        return  (credentials != null) ? credentials.getSpaceId() : null;
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
