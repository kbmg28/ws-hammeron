package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.dto.UserDto;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.VerificationToken;
import br.com.kbmg.wsmusiccontrol.repository.UserRepository;
import br.com.kbmg.wsmusiccontrol.repository.VerificationTokenRepository;
import br.com.kbmg.wsmusiccontrol.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends GenericServiceImpl<UserApp, UserRepository> implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Override
    public UserApp registerNewUserAccount(UserDto userDto) {

//        if (emailExist(userDto.getEmail())) {
//            throw new UserAlreadyExistException(
//                    "There is an account with that email adress: "
//                            + userDto.getEmail());
//        }

        UserApp userApp = new UserApp();
        userApp.setName(userDto.getName());
        userApp.setPassword(userDto.getPassword());
        userApp.setEmail(userDto.getEmail());

        return userRepository.save(userApp);
    }

    private boolean emailExist(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public UserApp getUser(String verificationToken) {
        return tokenRepository.findByToken(verificationToken).getUserApp();
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public void saveRegisteredUser(UserApp userApp) {
        userRepository.save(userApp);
    }

    @Override
    public void createVerificationToken(UserApp userApp, String token) {
        VerificationToken myToken = new VerificationToken(token, userApp);
        tokenRepository.save(myToken);
    }
}
