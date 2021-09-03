package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.config.messages.MessagesService;
import br.com.kbmg.wsmusiccontrol.constants.JwtConstants;
import br.com.kbmg.wsmusiccontrol.dto.ActivateUserAccountRefreshDto;
import br.com.kbmg.wsmusiccontrol.dto.LoginDto;
import br.com.kbmg.wsmusiccontrol.dto.UserDto;
import br.com.kbmg.wsmusiccontrol.dto.UserTokenHashDto;
import br.com.kbmg.wsmusiccontrol.event.OnRegistrationCompleteEvent;
import br.com.kbmg.wsmusiccontrol.exception.AuthorizationException;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.VerificationToken;
import br.com.kbmg.wsmusiccontrol.repository.VerificationTokenRepository;
import br.com.kbmg.wsmusiccontrol.service.JwtService;
import br.com.kbmg.wsmusiccontrol.service.SecurityService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.TOKEN_ACTIVATE_EXPIRED;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.USER_ACTIVATE_ACCOUNT;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.USER_OR_PASSWORD_INCORRECT;

@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    public MessagesService messagesService;

    @Override
    public String validateLoginAndGetToken(LoginDto loginDto) {
        String error = messagesService.get(USER_OR_PASSWORD_INCORRECT);
        UserApp userApp = userAppService
                .findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new AuthorizationException(error));

        boolean isCorrectPassword = BCrypt.checkpw(loginDto.getPassword(), userApp.getPassword());

        if (!isCorrectPassword) {
            throw new AuthorizationException(error);
        }

        if (!userApp.getEnabled()) {
            throw new AuthorizationException(messagesService.get(USER_ACTIVATE_ACCOUNT));
        }

        String token = jwtService.generateToken(loginDto, userApp);

        return String.format("%s%s", JwtConstants.BEARER, token);
    }

    @Override
    public void registerNewUserAccount(UserDto userDto, HttpServletRequest request) {
        UserApp registered = userAppService.registerNewUserAccount(userDto);

        publishEventSendMail(request, registered);
    }

    @Override
    public void activateUserAccount(UserTokenHashDto userTokenHashDto) {
        String errorMessage = messagesService.get(TOKEN_ACTIVATE_EXPIRED);
        UserApp userApp = userAppService.findByEmail(userTokenHashDto.getEmail())
                .orElseThrow( () -> new ServiceException(messagesService.get(TOKEN_ACTIVATE_EXPIRED)));

        if (userApp.getEnabled()) {
            return;
        }

        VerificationToken verificationToken = tokenRepository.findByTokenAndUserApp(userTokenHashDto.getTokenHash(), userApp);

        if(verificationToken != null && verificationToken.isValid()) {
            userAppService.saveUserEnabled(userApp);
        } else {
            throw new ServiceException(errorMessage);
        }
    }

    @Override
    public void createVerificationToken(UserApp userApp, String token) {
        VerificationToken myToken = new VerificationToken(token, userApp);
        tokenRepository.save(myToken);
    }

    @Override
    public void resendMailToken(ActivateUserAccountRefreshDto activateUserAccountRefreshDto, HttpServletRequest request) {
        userAppService.findByEmail(activateUserAccountRefreshDto.getEmail()).ifPresent(userApp -> {
            if (userApp.getEnabled()) {
                return;
            }

            tokenRepository.findByUserApp(userApp).ifPresent(token -> tokenRepository.delete(token));

            publishEventSendMail(request, userApp);
        });
    }

    private void publishEventSendMail(HttpServletRequest request, UserApp registered) {
        String appUrl = ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
                request.getLocale(), appUrl));
    }

}
