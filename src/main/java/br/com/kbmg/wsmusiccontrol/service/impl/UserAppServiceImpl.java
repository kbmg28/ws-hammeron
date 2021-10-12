package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.config.security.SpringSecurityUtil;
import br.com.kbmg.wsmusiccontrol.dto.user.RegisterDto;
import br.com.kbmg.wsmusiccontrol.dto.user.RegisterPasswordDto;
import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.UserPermission;
import br.com.kbmg.wsmusiccontrol.repository.UserAppRepository;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import br.com.kbmg.wsmusiccontrol.service.UserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.USER_ALREADY_EXISTS;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.USER_EMAIL_NOT_EXISTS;

@Service
public class UserAppServiceImpl extends GenericServiceImpl<UserApp, UserAppRepository> implements UserAppService {

    @Autowired
    private UserPermissionService userPermissionService;

    @Override
    public UserApp registerNewUserAccount(RegisterDto userDto) {
        AtomicReference<UserApp> userAppAtomicReference = new AtomicReference<>(null);

        repository.findByEmail(userDto.getEmail()).ifPresent(userAppInDatabase -> {
            if (userAppInDatabase.getEnabled()) {
                throw new ServiceException(messagesService.get(USER_ALREADY_EXISTS));
            }
            userAppAtomicReference.set(userAppInDatabase);
        });

        if (userAppAtomicReference.get() == null) {
            UserApp newUserApp = new UserApp();

            newUserApp.setEmail(userDto.getEmail());
            newUserApp.setName(userDto.getName());
            newUserApp.setCellPhone(userDto.getCellPhone());
            newUserApp.setEnabled(false);
            repository.save(newUserApp);

            userAppAtomicReference.set(newUserApp);
        }

        return userAppAtomicReference.get();
    }

    @Override
    public void registerUserPassword(RegisterPasswordDto registerPasswordDto) {
        this.findByEmail(registerPasswordDto.getEmail()).ifPresent(user -> this.encodePasswordAndSave(user, registerPasswordDto.getPassword()));
    }

    @Override
    public void encodePasswordAndSave(UserApp userApp, String password) {
        String hashpw = BCrypt.hashpw(password, BCrypt.gensalt());
        userApp.setPassword(hashpw);

        repository.save(userApp);
    }

    @Override
    public List<UserApp> findAllSysAdmin() {
        List<UserPermission> userPermissionList = userPermissionService.findByPermission(PermissionEnum.SYS_ADMIN);
        return userPermissionList.stream().map(UserPermission::getUserApp).collect(Collectors.toList());
    }

    @Override
    public UserApp findUserLogged() {
        return repository.findByEmail(SpringSecurityUtil.getEmail()).orElseThrow();
    }

    @Override
    public void saveUserEnabled(UserApp userApp) {
        userApp.setEnabled(true);
        repository.save(userApp);
    }


    @Override
    public UserApp findByEmailValidated(String email) {
        return repository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ServiceException(
                                messagesService.get(String.format(USER_EMAIL_NOT_EXISTS, email))
                        ));
    }

    @Override
    public Optional<UserApp> findByEmail(String email) {
        return repository.findByEmail(email);
    }

}
