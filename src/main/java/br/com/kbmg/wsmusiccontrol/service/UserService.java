package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.UserDto;
import br.com.kbmg.wsmusiccontrol.model.User;
import br.com.kbmg.wsmusiccontrol.model.VerificationToken;

public interface UserService extends GenericService<User>{
    User registerNewUserAccount(UserDto userDto);

    User getUser(String verificationToken);

    VerificationToken getVerificationToken(String VerificationToken);

    void saveRegisteredUser(User user);

    void createVerificationToken(User user, String token);
}
