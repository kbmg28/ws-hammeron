package builder;

import br.com.kbmg.wsmusiccontrol.dto.user.ActivateUserAccountRefreshDto;
import br.com.kbmg.wsmusiccontrol.dto.user.LoginDto;
import br.com.kbmg.wsmusiccontrol.dto.user.RegisterPasswordDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserTokenHashDto;
import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.UserPermission;
import br.com.kbmg.wsmusiccontrol.model.VerificationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static constants.BaseTestsConstants.AUTHENTICATED_USER_TEST_CELLPHONE;
import static constants.BaseTestsConstants.AUTHENTICATED_USER_TEST_NAME;
import static constants.BaseTestsConstants.AUTHENTICATED_USER_TEST_PASSWORD;
import static constants.BaseTestsConstants.TOKEN;
import static constants.BaseTestsConstants.generateRandomEmail;

public abstract class UserBuilder {

    public static UserApp generateUserAppLogged(PermissionEnum... permission) {

        UserApp userAppLoggedTest = new UserApp();

        userAppLoggedTest.setEmail(generateRandomEmail());
        userAppLoggedTest.setName(AUTHENTICATED_USER_TEST_NAME);
        userAppLoggedTest.setCellPhone(AUTHENTICATED_USER_TEST_CELLPHONE);
        userAppLoggedTest.setEnabled(true);
        String hashpw = BCrypt.hashpw(AUTHENTICATED_USER_TEST_PASSWORD, BCrypt.gensalt());
        userAppLoggedTest.setPassword(hashpw);

        return userAppLoggedTest;
    }

    public static Set<UserPermission> generateUserPermissions(UserApp userApp, PermissionEnum... permission) {
        return Arrays.stream(permission).map(pe -> {
            UserPermission userPermission = new UserPermission();

            userPermission.setPermission(pe);
//            userPermission.setUserApp(userApp);

            return userPermission;
        }).collect(Collectors.toSet());
    }

    public static UserDto generateUserDto(String email) {
        return new UserDto(AUTHENTICATED_USER_TEST_NAME, email, AUTHENTICATED_USER_TEST_CELLPHONE);
    }

    public static RegisterPasswordDto generateRegisterPasswordDto(String email) {
        return new RegisterPasswordDto(email, AUTHENTICATED_USER_TEST_PASSWORD);
    }

    public static UserTokenHashDto generateUserTokenHashDto(String email) {
        return new UserTokenHashDto(email, TOKEN);
    }

    public static ActivateUserAccountRefreshDto generateActivateUserAccountRefreshDto(String email) {
        return new ActivateUserAccountRefreshDto(email);
    }

    public static LoginDto generateLoginDto() {
        return generateLoginDto(generateRandomEmail(), AUTHENTICATED_USER_TEST_PASSWORD);
    }

    public static LoginDto generateLoginDto(String email, String pass) {
        return new LoginDto(email, pass);
    }

    public static VerificationToken generateVerificationToken(UserApp userApp) {
        return new VerificationToken(TOKEN, userApp);
    }

}
