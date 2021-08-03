package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.config.security.ValidateJwtTokenFilter;
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

    @Override
    public String validateLoginAndGetToken(LoginDto loginDto) {
        String error = "User or password incorrect";
        UserApp userApp = userAppService
                .findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new AuthorizationException(error));

        boolean isCorrectPassword = BCrypt.checkpw(loginDto.getPassword(), userApp.getPassword());

        if (!isCorrectPassword) {
            throw new AuthorizationException(error);
        }

        if (!userApp.getEnabled()) {
            throw new AuthorizationException("Please, activate the account");
        }

        String token = jwtService.generateToken(loginDto, userApp);

        return String.format("%s%s", ValidateJwtTokenFilter.BEARER, token);
    }

    @Override
    public void registerNewUserAccount(UserDto userDto, HttpServletRequest request) {
        UserApp registered = userAppService.registerNewUserAccount(userDto);

        publishEventSendMail(request, registered);
    }

    @Override
    public void activateUserAccount(UserTokenHashDto userTokenHashDto) {
        UserApp userApp = userAppService.findByEmailValidated(userTokenHashDto.getEmail());

        if (userApp.getEnabled()) {
            return;
        }

        VerificationToken verificationToken = tokenRepository.findByTokenAndUserApp(userTokenHashDto.getTokenHash(), userApp);

        if(verificationToken != null && verificationToken.isValid()) {
            userAppService.saveUserEnabled(userApp);
        } else {
            throw new ServiceException("Token to activate has expired");
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
