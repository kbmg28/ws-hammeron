package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.UserDto;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.VerificationToken;

public interface UserService extends GenericService<UserApp>{
    UserApp registerNewUserAccount(UserDto userDto);

    UserApp getUser(String verificationToken);

    VerificationToken getVerificationToken(String VerificationToken);

    void saveRegisteredUser(UserApp userApp);

    void createVerificationToken(UserApp userApp, String token);
}
