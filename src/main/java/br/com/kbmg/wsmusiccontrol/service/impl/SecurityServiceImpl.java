package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.config.messages.MessagesService;
import br.com.kbmg.wsmusiccontrol.constants.JwtConstants;
import br.com.kbmg.wsmusiccontrol.dto.user.ActivateUserAccountRefreshDto;
import br.com.kbmg.wsmusiccontrol.dto.user.LoginDto;
import br.com.kbmg.wsmusiccontrol.dto.user.RegisterDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserChangePasswordDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserTokenHashDto;
import br.com.kbmg.wsmusiccontrol.event.producer.PasswordRecoveryProducer;
import br.com.kbmg.wsmusiccontrol.event.producer.RegistrationProducer;
import br.com.kbmg.wsmusiccontrol.exception.AuthorizationException;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.VerificationToken;
import br.com.kbmg.wsmusiccontrol.repository.VerificationTokenRepository;
import br.com.kbmg.wsmusiccontrol.service.JwtService;
import br.com.kbmg.wsmusiccontrol.service.SecurityService;
import br.com.kbmg.wsmusiccontrol.service.SpaceUserAppAssociationService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.DATA_INVALID;
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
    private SpaceUserAppAssociationService spaceUserAppAssociationService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    public MessagesService messagesService;

    @Autowired
    private PasswordRecoveryProducer passwordRecoveryProducer;

    @Autowired
    private RegistrationProducer registrationProducer;

    @Override
    public String validateLoginAndGetToken(LoginDto loginDto) {
        String email = loginDto.getEmail();
        String error = messagesService.get(USER_OR_PASSWORD_INCORRECT);
        UserApp userApp = userAppService
                .findByEmail(email)
                .orElseThrow(() -> new AuthorizationException(email, error));

        validatePassword(email, loginDto.getPassword(), userApp.getPassword(), error);

        if (!userApp.getEnabled()) {
            throw new AuthorizationException(email, messagesService.get(USER_ACTIVATE_ACCOUNT));
        }

        String token = jwtService.generateToken(loginDto, userApp);

        return String.format("%s%s", JwtConstants.BEARER, token);
    }

    @Override
    public void registerNewUserAccount(RegisterDto userDto, HttpServletRequest request) {
        UserApp registered = userAppService.registerNewUserAccount(userDto);

        registrationProducer.publishEvent(request, registered);
    }

    @Override
    public void activateUserAccount(UserTokenHashDto userTokenHashDto) {
        String errorMessage = messagesService.get(TOKEN_ACTIVATE_EXPIRED);
        UserApp userApp = userAppService.findByEmail(userTokenHashDto.getEmail())
                .orElseThrow( () -> new ServiceException(errorMessage));

        if (userApp.getEnabled()) {
            return;
        }

        VerificationToken verificationToken = tokenRepository.findByTokenAndUserApp(userTokenHashDto.getTokenHash(), userApp);

        if(verificationToken != null && verificationToken.isValid()) {
            userAppService.saveUserEnabled(userApp);
        } else {
            throw new ServiceException(errorMessage);
        }

        spaceUserAppAssociationService.createAssociationWithPublicSpace(userApp);
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

            registrationProducer.publishEvent(request, userApp);
        });
    }

    @Override
    public void passwordRecovery(ActivateUserAccountRefreshDto activateUserAccountRefreshDto, HttpServletRequest request) {
        userAppService.findByEmail(activateUserAccountRefreshDto.getEmail())
                .ifPresent(userApp -> passwordRecoveryProducer.publishEvent(request, userApp));
    }

    @Override
    public void passwordRecoveryChange(UserChangePasswordDto userChangePasswordDto) {
        String defaultError = messagesService.get(DATA_INVALID);
        String email = userChangePasswordDto.getEmail();
        UserApp userApp = userAppService.findByEmail(email).orElseThrow(() -> new AuthorizationException(email, defaultError));

        validatePassword(email, userChangePasswordDto.getTemporaryPassword(), userApp.getPassword(), defaultError);

        userAppService.encodePasswordAndSave(userApp, userChangePasswordDto.getNewPassword());
    }

    private void validatePassword(String email, String plainTextPassword, String hashPassword, String error) {
        boolean isCorrectPassword = BCrypt.checkpw(plainTextPassword, hashPassword);

        if (!isCorrectPassword) {
            throw new AuthorizationException(email, error);
        }
    }

}
