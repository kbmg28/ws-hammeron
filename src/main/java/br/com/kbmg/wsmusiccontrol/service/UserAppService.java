package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.user.RegisterDto;
import br.com.kbmg.wsmusiccontrol.dto.user.RegisterPasswordDto;
import br.com.kbmg.wsmusiccontrol.model.UserApp;

import java.util.List;
import java.util.Optional;

public interface UserAppService extends GenericService<UserApp>{
    UserApp registerNewUserAccount(RegisterDto userDto);
    void saveUserEnabled(UserApp userApp);
    UserApp findByEmailValidated(String email);
    Optional<UserApp> findByEmail(String email);
    void registerUserPassword(RegisterPasswordDto registerPasswordDto);
    void encodePasswordAndSave(UserApp userApp, String password);

    List<UserApp> findAllSysAdmin();

    UserApp findUserLogged();
}
