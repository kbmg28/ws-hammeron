package builder;

import br.com.kbmg.wsmusiccontrol.dto.user.ActivateUserAccountRefreshDto;
import br.com.kbmg.wsmusiccontrol.dto.user.LoginDto;
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

import static constants.BaseTestsConstants.*;

public abstract class UserBuilder {

    public static UserApp generateUserAppLogged(PermissionEnum... permission) {

        UserApp userAppLoggedTest = new UserApp();

        userAppLoggedTest.setEmail(AUTHENTICATED_USER_TEST_EMAIL);
        userAppLoggedTest.setName(AUTHENTICATED_USER_TEST_NAME);
        userAppLoggedTest.setCellPhone(AUTHENTICATED_USER_TEST_CELLPHONE);
        userAppLoggedTest.setEnabled(true);
        String hashpw = BCrypt.hashpw(AUTHENTICATED_USER_TEST_PASSWORD, BCrypt.gensalt());
        userAppLoggedTest.setPassword(hashpw);

        return userAppLoggedTest;
    }

    public static Set<UserPermission> generateUserPermissions(UserApp userApp, PermissionEnum... permission) {
        return Arrays.stream(permission).map(pe -> new UserPermission() {{
            setPermission(pe);
            setUserApp(userApp);
        }}).collect(Collectors.toSet());
    }

    public static UserDto generateUserDto() {
        return new UserDto(AUTHENTICATED_USER_TEST_NAME, AUTHENTICATED_USER_TEST_EMAIL, AUTHENTICATED_USER_TEST_PASSWORD, AUTHENTICATED_USER_TEST_CELLPHONE);
    }

    public static UserTokenHashDto generateUserTokenHashDto() {
        return new UserTokenHashDto(AUTHENTICATED_USER_TEST_EMAIL, TOKEN);
    }

    public static ActivateUserAccountRefreshDto generateActivateUserAccountRefreshDto() {
        return new ActivateUserAccountRefreshDto(AUTHENTICATED_USER_TEST_EMAIL);
    }

    public static LoginDto generateLoginDto() {
        return generateLoginDto(AUTHENTICATED_USER_TEST_EMAIL, AUTHENTICATED_USER_TEST_PASSWORD);
    }

    public static LoginDto generateLoginDto(String email, String pass) {
        return new LoginDto(email, pass);
    }

    public static VerificationToken generateVerificationToken(UserApp userApp) {
        return new VerificationToken(TOKEN, userApp);
    }

}
