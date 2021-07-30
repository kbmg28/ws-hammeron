package br.com.kbmg.wsmusiccontrol.config.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@EqualsAndHashCode
@Getter
public class UserCredentialsSecurity {

    private final String request;
    private final String email;
    private final List<String> roles;

    public UserCredentialsSecurity(UserDetails userDetails, HttpServletRequest request) {
        this.email = userDetails.getUsername();
        this.request = request.getRequestURI();
        this.roles = userDetails
                .getAuthorities()
                .stream()
                .map(a -> a.getAuthority().replaceAll("ROLE_", ""))
                .collect(Collectors.toList());
    }
}
