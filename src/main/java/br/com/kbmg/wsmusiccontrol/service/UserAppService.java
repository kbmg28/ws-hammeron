package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.user.UserDto;
import br.com.kbmg.wsmusiccontrol.model.UserApp;

import java.util.Optional;

public interface UserAppService extends GenericService<UserApp>{
    UserApp registerNewUserAccount(UserDto userDto);
    void saveUserEnabled(UserApp userApp);
    UserApp findByEmailValidated(String email);
    Optional<UserApp> findByEmail(String email);
}
