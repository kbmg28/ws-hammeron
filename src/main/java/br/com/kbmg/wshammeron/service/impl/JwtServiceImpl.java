package br.com.kbmg.wshammeron.service.impl;

import br.com.kbmg.wshammeron.config.messages.MessagesService;
import br.com.kbmg.wshammeron.constants.AppConstants;
import br.com.kbmg.wshammeron.constants.JwtConstants;
import br.com.kbmg.wshammeron.constants.KeyMessageConstants;
import br.com.kbmg.wshammeron.dto.user.LoginDto;
import br.com.kbmg.wshammeron.exception.AuthorizationException;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.service.JwtService;
import br.com.kbmg.wshammeron.service.SpaceUserAppAssociationService;
import br.com.kbmg.wshammeron.service.UserAppService;
import br.com.kbmg.wshammeron.service.UserPermissionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiration}")
    private String expiration;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private UserPermissionService userPermissionService;

    @Autowired
    private SpaceUserAppAssociationService spaceUserAppAssociationService;

    @Autowired
    public MessagesService messagesService;

    @Override
    public String generateToken(@Valid LoginDto loginDto, UserApp userApp, Space lastAccessedSpace) {
        Date today = new Date();
        Date expirationDate = new Date(today.getTime() + Long.parseLong(expiration));
        List<String> permissions = userPermissionService.findAllBySpaceAndUserApp(lastAccessedSpace, userApp);
        return buildToken(today, expirationDate, lastAccessedSpace, userApp, permissions);
    }

    @Override
    public boolean isValidToken(String token) {
        try {
            Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String validateTokenAndGetUserId(String token) {
        if (!isValidToken(token)){
            throw new AuthorizationException(null, messagesService.get(KeyMessageConstants.TOKEN_JWT_INVALID));
        }

        Claims claims = getAllClaims(token);

        return claims.getSubject();
    }

    @Override
    public String getValue(String token, String key) {
        Claims claims = getAllClaims(token);

        return claims.get(key, String.class);
    }

    @Override
    public String updateSpaceOnToken(HttpServletRequest request, Space space) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        String jwtToken = authorization.substring(7, authorization.length());
        Claims claims = getAllClaims(jwtToken);

        Date startDate = claims.getIssuedAt();
        Date expirationDate = claims.getExpiration();
        String userId = claims.getSubject();

        UserApp userApp = userAppService.findByIdValidated(userId, "");

        List<String> permissions = userPermissionService.findAllBySpaceAndUserApp(space, userApp);

        return buildToken(startDate, expirationDate, space, userApp, permissions);
    }

    private Claims getAllClaims(String jwtToken) {
        return Jwts.parser()
                .setSigningKey(this.secret)
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    private String buildToken(Date startDate, Date expirationDate, Space lastSpace, UserApp userApp, List<String> permissions) {
        String pureJwt = Jwts.builder()
                .setIssuer(AppConstants.API_DESCRIBE)
                .setSubject(userApp.getId())
                .setIssuedAt(startDate)
                .claim(JwtConstants.CLAIM_EMAIL, userApp.getEmail())
                .claim(JwtConstants.CLAIM_NAME, userApp.getName())
                .claim(JwtConstants.CLAIM_SPACE_ID, lastSpace.getId())
                .claim(JwtConstants.CLAIM_SPACE_NAME, lastSpace.getName())
                .claim(JwtConstants.CLAIM_PERMISSIONS, permissions)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        return String.format("%s%s", JwtConstants.BEARER, pureJwt);
    }

}
