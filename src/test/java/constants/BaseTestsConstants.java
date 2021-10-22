package constants;

import java.util.UUID;

import static br.com.kbmg.wsmusiccontrol.constants.JwtConstants.BEARER;

public abstract class BaseTestsConstants {

    public static final String ANY_VALUE = "Any value " + UUID.randomUUID();

    public static final String TOKEN =BEARER.concat("tokenTest");
    public static final String AUTHENTICATED_USER_TEST_EMAIL = "integration_test@test.com";
    public static final String AUTHENTICATED_USER_TEST_NAME = "Integration test name";
    public static final String AUTHENTICATED_USER_TEST_PASSWORD = "123456";
    public static final String AUTHENTICATED_USER_TEST_CELLPHONE = "47984366284";

}
