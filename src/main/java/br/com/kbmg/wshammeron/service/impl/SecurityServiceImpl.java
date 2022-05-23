package br.com.kbmg.wshammeron.service.impl;

import br.com.kbmg.wshammeron.config.messages.MessagesService;
import br.com.kbmg.wshammeron.dto.user.ActivateUserAccountRefreshDto;
import br.com.kbmg.wshammeron.dto.user.LoginDto;
import br.com.kbmg.wshammeron.dto.user.RegisterDto;
import br.com.kbmg.wshammeron.dto.user.UserChangePasswordDto;
import br.com.kbmg.wshammeron.dto.user.UserTokenHashDto;
import br.com.kbmg.wshammeron.event.producer.PasswordRecoveryProducer;
import br.com.kbmg.wshammeron.event.producer.RegistrationProducer;
import br.com.kbmg.wshammeron.exception.BadUserInfoException;
import br.com.kbmg.wshammeron.exception.ServiceException;
import br.com.kbmg.wshammeron.model.SpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.model.VerificationToken;
import br.com.kbmg.wshammeron.repository.VerificationTokenRepository;
import br.com.kbmg.wshammeron.service.JwtService;
import br.com.kbmg.wshammeron.service.SecurityService;
import br.com.kbmg.wshammeron.service.SpaceUserAppAssociationService;
import br.com.kbmg.wshammeron.service.UserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Set;

import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.DATA_INVALID;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.TOKEN_ACTIVATE_EXPIRED;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.USER_ACTIVATE_ACCOUNT;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.USER_OR_PASSWORD_INCORRECT;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.USER_PASSWORD_EXPIRED;

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
    public MessagesService messagesService;

    @Autowired
    private PasswordRecoveryProducer passwordRecoveryProducer;

    @Autowired
    private RegistrationProducer registrationProducer;

    @Override
    public String validateLoginAndGetToken(@Valid LoginDto loginDto) {
        String email = loginDto.getEmail().toLowerCase();
        String error = messagesService.get(USER_OR_PASSWORD_INCORRECT);
        UserApp userApp = userAppService
                .findByEmail(email)
                .orElseThrow(() -> new BadUserInfoException(email, error));

        if (Boolean.FALSE.equals(userApp.getEnabled())) {
            throw new BadUserInfoException(email, messagesService.get(USER_ACTIVATE_ACCOUNT));
        }

        if (userApp.isExpiredPassword()) {
            throw new BadUserInfoException(email, messagesService.get(USER_PASSWORD_EXPIRED));
        }
        validatePassword(email, loginDto.getPassword(), userApp.getPassword(), error);
        SpaceUserAppAssociation lastAccessedSpace = spaceUserAppAssociationService.findLastAccessedSpace(userApp);

        return jwtService.generateToken(loginDto, userApp, lastAccessedSpace.getSpace());
    }

    @Override
    public void registerNewUserAccount(@Valid RegisterDto userDto, HttpServletRequest request) {
        UserApp registered = userAppService.registerNewUserAccount(userDto);

        registrationProducer.publishEvent(request, registered);
    }

    @Override
    public void activateUserAccount(@Valid UserTokenHashDto userTokenHashDto) {
        String errorMessage = messagesService.get(TOKEN_ACTIVATE_EXPIRED);
        UserApp userApp = userAppService.findByEmail(userTokenHashDto.getEmail().toLowerCase())
                .orElseThrow( () -> new ServiceException(errorMessage));

        if (Boolean.TRUE.equals(userApp.getEnabled())) {
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
    public void resendMailToken(@Valid ActivateUserAccountRefreshDto activateUserAccountRefreshDto, HttpServletRequest request) {
        userAppService.findByEmail(activateUserAccountRefreshDto.getEmail().toLowerCase()).ifPresent(userApp -> {
            if (Boolean.TRUE.equals(userApp.getEnabled())) {
                return;
            }

            deleteOldTokenIfExists(userApp);

            registrationProducer.publishEvent(request, userApp);
        });
    }

    @Override
    public void passwordRecovery(@Valid ActivateUserAccountRefreshDto activateUserAccountRefreshDto, HttpServletRequest request) {
        userAppService.findByEmail(activateUserAccountRefreshDto.getEmail().toLowerCase())
                .ifPresent(userApp -> passwordRecoveryProducer.publishEvent(request, userApp));
    }

    @Override
    public void passwordRecoveryChange(@Valid UserChangePasswordDto userChangePasswordDto) {
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
