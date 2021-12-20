package br.com.kbmg.wsmusiccontrol.constants;

public abstract class AppConstants {
    public static final String LANGUAGE = "language";
    public static final String SYS_ADMIN = "SYS_ADMIN";
    public static final String APP_NAME = "HammerOn";
    public static final String API_DESCRIBE = APP_NAME.concat(" API: Easy control the band's songs");
    public static final String DEFAULT_SPACE = "Default";

    public static boolean isRunningProdProfile(String profile) {
        return "heroku-prd".equals(profile);
    }
}
