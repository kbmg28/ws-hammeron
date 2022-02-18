package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.config.messages.MessagesService;
import br.com.kbmg.wsmusiccontrol.dto.user.ActivateUserAccountRefreshDto;
import br.com.kbmg.wsmusiccontrol.dto.user.LoginDto;
import br.com.kbmg.wsmusiccontrol.dto.user.RegisterDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserChangePasswordDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserTokenHashDto;
import br.com.kbmg.wsmusiccontrol.event.producer.PasswordRecoveryProducer;
import br.com.kbmg.wsmusiccontrol.event.producer.RegistrationProducer;
import br.com.kbmg.wsmusiccontrol.exception.BadUserInfoException;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.SpaceUserAppAssociation;
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
import java.time.LocalDateTime;
import java.util.Set;

import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.DATA_INVALID;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.TOKEN_ACTIVATE_EXPIRED;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.USER_ACTIVATE_ACCOUNT;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.USER_OR_PASSWORD_INCORRECT;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.USER_PASSWORD_EXPIRED;

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
        String email = loginDto.getEmail().toLowerCase();
        String error = messagesService.get(USER_OR_PASSWORD_INCORRECT);
        UserApp userApp = userAppService
                .findByEmail(email)
                .orElseThrow(() -> new BadUserInfoException(email, error));

        if (!userApp.getEnabled()) {
            throw new BadUserInfoException(email, messagesService.get(USER_ACTIVATE_ACCOUNT));
        }

        if (userApp.isExpiredPassword()) {
            throw new BadUserInfoException(email, messagesService.get(USER_PASSWORD_EXPIRED));
        }
        validatePassword(email, loginDto.getPassword(), userApp.getPassword(), error);
        SpaceUserAppAssociation lastAccessedSpace = spaceUserAppAssociationService.findLastAccessedSpace(userApp);
        String token = jwtService.generateToken(loginDto, userApp, lastAccessedSpace.getSpace());

        return token;
    }

    @Override
    public void registerNewUserAccount(RegisterDto userDto, HttpServletRequest request) {
        UserApp registered = userAppService.registerNewUserAccount(userDto);

        registrationProducer.publishEvent(request, registered);
    }

    @Override
    public void activateUserAccount(UserTokenHashDto userTokenHashDto) {
        String errorMessage = messagesService.get(TOKEN_ACTIVATE_EXPIRED);
        UserApp userApp = userAppService.findByEmail(userTokenHashDto.getEmail().toLowerCase())
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

        Set<SpaceUserAppAssociation> spaceUserAppAssociationList = userApp.getSpaceUserAppAssociationList();

        if (spaceUserAppAssociationList.isEmpty()) {
            spaceUserAppAssociationService.createAssociationWithPublicSpace(userApp);
        } else {
            SpaceUserAppAssociation spaceUserAppAssociation = spaceUserAppAssociationList.stream().findFirst().orElseThrow();
            spaceUserAppAssociationService.updateLastAccessedSpace(userApp, spaceUserAppAssociation.getSpace());
        }
    }

    @Override
    public void createVerificationToken(UserApp userApp, String token) {
        deleteOldTokenIfExists(userApp);
        VerificationToken myToken = new VerificationToken(token, userApp);
        tokenRepository.save(myToken);
    }

    @Override
    public void resendMailToken(ActivateUserAccountRefreshDto activateUserAccountRefreshDto, HttpServletRequest request) {
        userAppService.findByEmail(activateUserAccountRefreshDto.getEmail().toLowerCase()).ifPresent(userApp -> {
            if (userApp.getEnabled()) {
                return;
            }

            deleteOldTokenIfExists(userApp);

            registrationProducer.publishEvent(request, userApp);
        });
    }

    @Override
    public void passwordRecovery(ActivateUserAccountRefreshDto activateUserAccountRefreshDto, HttpServletRequest request) {
        userAppService.findByEmail(activateUserAccountRefreshDto.getEmail().toLowerCase())
                .ifPresent(userApp -> passwordRecoveryProducer.publishEvent(request, userApp));
    }

    @Override
    public void passwordRecoveryChange(UserChangePasswordDto userChangePasswordDto) {
        String defaultError = messagesService.get(DATA_INVALID);
        String email = userChangePasswordDto.getEmail().toLowerCase();
        UserApp userApp = userAppService
                .findByEmail(email)
                .orElseThrow(() -> new BadUserInfoException(email, defaultError));

        validatePassword(email, userChangePasswordDto.getTemporaryPassword(), userApp.getPassword(), defaultError);
        LocalDateTime expireDate = LocalDateTime.now().plusYears(1);
        userAppService.encodePasswordAndSave(userApp, userChangePasswordDto.getNewPassword(), expireDate);
    }

    private void deleteOldTokenIfExists(UserApp userApp) {
        tokenRepository.findByUserApp(userApp).ifPresent(token -> tokenRepository.delete(token));
    }

    private void validatePassword(String email, String plainTextPassword, String hashPassword, String error) {
        boolean isCorrectPassword = BCrypt.checkpw(plainTextPassword, hashPassword);

        if (!isCorrectPassword) {
            throw new BadUserInfoException(email, error);
        }
    }

}
