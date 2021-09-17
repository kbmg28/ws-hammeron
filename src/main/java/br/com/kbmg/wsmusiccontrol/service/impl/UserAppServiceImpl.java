package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.dto.user.RegisterDto;
import br.com.kbmg.wsmusiccontrol.dto.user.RegisterPasswordDto;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.repository.UserAppRepository;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.USER_ALREADY_EXISTS;
import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.USER_EMAIL_NOT_EXISTS;

@Service
public class UserAppServiceImpl extends GenericServiceImpl<UserApp, UserAppRepository> implements UserAppService {

    @Override
    public UserApp registerNewUserAccount(RegisterDto userDto) {
        if (repository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ServiceException(messagesService.get(USER_ALREADY_EXISTS));
        }

        UserApp userApp = new UserApp();

        userApp.setEmail(userDto.getEmail());
        userApp.setName(userDto.getName());
        userApp.setCellPhone(userDto.getCellPhone());
        userApp.setEnabled(false);

        return repository.save(userApp);
    }

    @Override
    public void registerUserPassword(RegisterPasswordDto registerPasswordDto) {
        this.findByEmail(registerPasswordDto.getEmail()).ifPresent(user -> {

            String hashpw = BCrypt.hashpw(registerPasswordDto.getPassword(), BCrypt.gensalt());
            user.setPassword(hashpw);

            repository.save(user);
        });
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
