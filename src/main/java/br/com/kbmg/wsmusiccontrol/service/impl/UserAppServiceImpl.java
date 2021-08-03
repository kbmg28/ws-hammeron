package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.dto.UserDto;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.repository.UserAppRepository;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAppServiceImpl extends GenericServiceImpl<UserApp, UserAppRepository> implements UserAppService {

    @Autowired
    private UserAppRepository userAppRepository;

    @Override
    public UserApp registerNewUserAccount(UserDto userDto) {
        if (userAppRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ServiceException("User already exists");
        }

        UserApp userApp = new UserApp();

        userApp.setEmail(userDto.getEmail());
        userApp.setName(userDto.getName());

        String hashpw = BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt());
        userApp.setPassword(hashpw);
        userApp.setEnabled(false);

        return userAppRepository.save(userApp);
    }

    @Override
    public void saveUserEnabled(UserApp userApp) {
        userApp.setEnabled(true);
        userAppRepository.save(userApp);
    }


    @Override
    public UserApp findByEmailValidated(String email) {
        return userAppRepository.findByEmail(email).orElseThrow(() -> new ServiceException("User with this email does not exists"));
    }

    @Override
    public Optional<UserApp> findByEmail(String email) {
        return userAppRepository.findByEmail(email);
    }
}
