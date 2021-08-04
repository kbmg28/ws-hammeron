package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.config.messages.MessagesService;
import br.com.kbmg.wsmusiccontrol.constants.AppConstants;
import br.com.kbmg.wsmusiccontrol.constants.JwtConstants;
import br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants;
import br.com.kbmg.wsmusiccontrol.dto.LoginDto;
import br.com.kbmg.wsmusiccontrol.exception.AuthorizationException;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.service.JwtService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

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

        return Jwts.builder()
                .setIssuer(AppConstants.API_DESCRIBE)
                .setSubject(userApp.getId().toString())
                .setIssuedAt(today)
                .claim(JwtConstants.CLAIM_EMAIL, userApp.getEmail())
                .claim(JwtConstants.CLAIM_NAME, userApp.getName())
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
    public Long validateTokenAndGetUserId(String token) {
        if (!isValidToken(token)){
            throw new AuthorizationException(messagesService.get(KeyMessageConstants.TOKEN_JWT_INVALID));
        }

        Claims claims = Jwts.parser()
                .setSigningKey(this.secret).parseClaimsJws(token).getBody();

        return Long.parseLong(claims.getSubject());
    }
}
