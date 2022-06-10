package builder;

import br.com.kbmg.wshammeron.dto.user.ActivateUserAccountRefreshDto;
import br.com.kbmg.wshammeron.dto.user.LoginDto;
import br.com.kbmg.wshammeron.dto.user.RegisterDto;
import br.com.kbmg.wshammeron.dto.user.RegisterPasswordDto;
import br.com.kbmg.wshammeron.dto.user.UserChangePasswordDto;
import br.com.kbmg.wshammeron.dto.user.UserDto;
import br.com.kbmg.wshammeron.dto.user.UserTokenHashDto;
import br.com.kbmg.wshammeron.dto.user.UserWithPermissionDto;
import br.com.kbmg.wshammeron.dto.user.UserWithSinglePermissionDto;
import br.com.kbmg.wshammeron.enums.PermissionEnum;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.SpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.model.UserPermission;
import br.com.kbmg.wshammeron.model.VerificationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static constants.BaseTestsConstants.ANY_VALUE;
import static constants.BaseTestsConstants.BEARER_TOKEN_TEST;
import static constants.BaseTestsConstants.USER_TEST_CELLPHONE;
import static constants.BaseTestsConstants.USER_TEST_NAME;
import static constants.BaseTestsConstants.USER_TEST_PASSWORD;
import static constants.BaseTestsConstants.generateRandomEmail;
import static java.sql.Timestamp.valueOf;

public abstract class UserBuilder {

    public static UserApp generateUserAppLogged() {

        UserApp userAppLoggedTest = new UserApp();

        userAppLoggedTest.setEmail(generateRandomEmail());
        userAppLoggedTest.setName(USER_TEST_NAME);
        userAppLoggedTest.setCellPhone(USER_TEST_CELLPHONE);
        userAppLoggedTest.setEnabled(true);
        userAppLoggedTest.setIsSysAdmin(false);
        String hashpw = BCrypt.hashpw(USER_TEST_PASSWORD, BCrypt.gensalt());
        userAppLoggedTest.setPassword(hashpw);
        userAppLoggedTest.setPasswordExpireDate(LocalDateTime.now().plusDays(2));
        return userAppLoggedTest;
    }

    public static UserApp generateOtherUserApp() {

        UserApp userApp = new UserApp();

        userApp.setEmail(generateRandomEmail());
        userApp.setName(ANY_VALUE);
        userApp.setCellPhone(ANY_VALUE);
        userApp.setEnabled(true);
        userApp.setIsSysAdmin(false);
        String hashpw = BCrypt.hashpw(USER_TEST_PASSWORD, BCrypt.gensalt());
        userApp.setPassword(hashpw);
        userApp.setPasswordExpireDate(LocalDateTime.now().plusDays(2));
        return userApp;
    }

    public static SpaceUserAppAssociation generateSpaceUserAppAssociation(UserApp userApp,
                                                                          Space space) {
        return generateSpaceUserAppAssociation(userApp, space, null);
    }

    public static SpaceUserAppAssociation generateSpaceUserAppAssociation(UserApp userApp,
                                                                          Space space,
                                                                          PermissionEnum permission) {
        SpaceUserAppAssociation spaceUserAppAssociation = new SpaceUserAppAssociation();

        spaceUserAppAssociation.setId(UUID.randomUUID().toString());
        spaceUserAppAssociation.setSpace(space);
        spaceUserAppAssociation.setUserApp(userApp);
        spaceUserAppAssociation.setLastAccessedSpace(true);
        spaceUserAppAssociation.setActive(true);

        userApp.getSpaceUserAppAssociationList().add(spaceUserAppAssociation);

        if(permission != null) {
            generateUserPermission(spaceUserAppAssociation, permission);
        }

        return spaceUserAppAssociation;
    }

    public static UserPermission generateUserPermission(SpaceUserAppAssociation spaceUserAppAssociation,
                                               PermissionEnum permission) {
        UserPermission userPermission = new UserPermission();

        userPermission.setId(UUID.randomUUID().toString());
        userPermission.setPermission(permission);
        userPermission.setSpaceUserAppAssociation(spaceUserAppAssociation);

        spaceUserAppAssociation.getUserPermissionList().add(userPermission);

        return userPermission;
    }

    public static UserDto generateUserDto(String email) {
        return new UserDto(USER_TEST_NAME, email, USER_TEST_CELLPHONE);
    }

    public static UserWithPermissionDto generateUserWithPermissionDto(UserApp userApp, PermissionEnum... permissions) {
        return new UserWithPermissionDto(userApp.getId(), USER_TEST_NAME,
                userApp.getEmail(), USER_TEST_CELLPHONE, Set.of(permissions));
    }

    public static UserWithSinglePermissionDto generateUserWithPermissionDto(UserWithPermissionDto userWithPermissionDto) {
        return new UserWithSinglePermissionDto(userWithPermissionDto.getName(), PermissionEnum.PARTICIPANT);
    }

    public static RegisterPasswordDto generateRegisterPasswordDto(String email) {
        return new RegisterPasswordDto(email, USER_TEST_PASSWORD);
    }

    public static UserTokenHashDto generateUserTokenHashDto(String email) {
        return new UserTokenHashDto(email, BEARER_TOKEN_TEST);
    }

    public static ActivateUserAccountRefreshDto generateActivateUserAccountRefreshDto(String email) {
        return new ActivateUserAccountRefreshDto(email);
    }

    public static LoginDto generateLoginDto() {
        return generateLoginDto(generateRandomEmail(), USER_TEST_PASSWORD);
    }

    public static LoginDto generateLoginDto(String email, String pass) {
        return new LoginDto(email, pass);
    }

    public static VerificationToken generateVerificationToken(UserApp userApp) {
        VerificationToken verificationToken = new VerificationToken(BEARER_TOKEN_TEST, userApp);
        Date expireDate = valueOf(LocalDateTime.now().plusDays(1));
        verificationToken.setExpiryDate(expireDate);

        return verificationToken;
    }

    public static RegisterDto generateRegisterDto(String email) {
        return new RegisterDto(USER_TEST_NAME, email, USER_TEST_CELLPHONE);
    }

    public static UserChangePasswordDto generateUserChangePasswordDto(UserApp userApp) {
        return new UserChangePasswordDto(userApp.getEmail(), USER_TEST_PASSWORD, USER_TEST_PASSWORD);
    }

}
