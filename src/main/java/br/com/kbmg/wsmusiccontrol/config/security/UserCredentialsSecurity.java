package br.com.kbmg.wsmusiccontrol.config.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ToString
@EqualsAndHashCode
@Getter
public class UserCredentialsSecurity {

    private final String requestId = UUID.randomUUID().toString();
    private final String requestURI;
    private final String email;
    private final List<String> roles;

    public UserCredentialsSecurity(UserDetails userDetails, HttpServletRequest requestURI) {
        this.email = userDetails.getUsername();
        this.requestURI = requestURI.getRequestURI();
        this.roles = userDetails
                .getAuthorities()
                .stream()
                .map(a -> a.getAuthority().replaceAll("ROLE_", ""))
                .collect(Collectors.toList());
    }

    public UserCredentialsSecurity(String email) {
        this.email = email;
        this.requestURI = null;
        this.roles = null;
    }
}
