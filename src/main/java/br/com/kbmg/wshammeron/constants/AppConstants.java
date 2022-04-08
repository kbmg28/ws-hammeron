package br.com.kbmg.wshammeron.constants;

import java.util.HashMap;
import java.util.Map;

public abstract class AppConstants {
    private static final String HML_PROFILE = "heroku-hml";
    private static final String PRD_PROFILE = "heroku-prd";

    public static final String LANGUAGE = "language";
    public static final String SYS_ADMIN = "SYS_ADMIN";
    public static final String APP_NAME = "HammerOn";
    public static final String API_DESCRIBE = APP_NAME.concat(" API: Easy control the band's songs");
    public static final String DEFAULT_SPACE = "Default";

    public static boolean isRunningProdProfile(String profile) {
        return PRD_PROFILE.equals(profile);
    }

    public static String getFrontUrl(String profile) {
        Map<String, String> map = new HashMap<>();

        map.put(HML_PROFILE, "https://hammeron-hml.herokuapp.com");
        map.put(PRD_PROFILE, "https://hammeron.org");

        return map.getOrDefault(profile, "http://localhost:4200/");
    }
}
