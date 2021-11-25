package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.config.messages.MessagesService;
import br.com.kbmg.wsmusiccontrol.constants.AppConstants;
import br.com.kbmg.wsmusiccontrol.constants.JwtConstants;
import br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants;
import br.com.kbmg.wsmusiccontrol.dto.user.LoginDto;
import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.exception.AuthorizationException;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.UserPermission;
import br.com.kbmg.wsmusiccontrol.service.JwtService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiration}")
    private String expiration;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private UserAppService userAppService;

    @Autowired
    public MessagesService messagesService;

    @Override
    public String generateToken(LoginDto loginDto, UserApp userApp) {

        Date today = new Date();
        Date expirationDate = new Date(today.getTime() + Long.parseLong(expiration));
        Set<PermissionEnum> permissionList = userApp.getUserPermissionList().stream().map(UserPermission::getPermission).collect(Collectors.toSet());
        return Jwts.builder()
                .setIssuer(AppConstants.API_DESCRIBE)
                .setSubject(userApp.getId())
                .setIssuedAt(today)
                .claim(JwtConstants.CLAIM_EMAIL, userApp.getEmail())
                .claim(JwtConstants.CLAIM_NAME, userApp.getName())
                .claim(JwtConstants.CLAIM_PERMISSIONS, permissionList)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
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

        Claims claims = Jwts.parser()
                .setSigningKey(this.secret).parseClaimsJws(token).getBody();

        return claims.getSubject();
    }
}
