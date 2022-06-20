package constants;

import java.util.UUID;

import static br.com.kbmg.wshammeron.constants.JwtConstants.BEARER;

public abstract class BaseTestsConstants {

    public static final String ANY_VALUE = "Any value " + UUID.randomUUID();

    public static final String TOKEN_TEST = "tokenTest";
    public static final String BEARER_TOKEN_TEST =BEARER.concat(TOKEN_TEST);

    public static final String SECRET_UNIT_TEST =  "secretUnitTest";
    public static final String USER_TEST_NAME = "Integration test name";
    public static final String USER_TEST_PASSWORD = "739qpf*";
    public static final String USER_TEST_CELLPHONE = "47984366284";

    public static final String EVENT_TEST_CELLPHONE = "47984366284";

    public static final String LINK_YOUTUBE_MUSIC_TEST = "https://youtu.be/aJRu5ltxXjc";
    public static final String LINK_SPOTIFY_MUSIC_TEST = "https://open.spotify.com/track/1bEnIDpwKsyhDauHVoMz6t?si=c07652b2628e4485";
    public static final String LINK_CHORD_MUSIC_TEST = "https://www.cifraclub.com.br/naruto/blue-bird/";

    public static String generateRandomEmail(){
        return "integration_test_"+ UUID.randomUUID().toString().replaceAll("-", "") +"@test.com";
    }
}
