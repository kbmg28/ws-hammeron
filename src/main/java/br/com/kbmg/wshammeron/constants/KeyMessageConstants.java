package br.com.kbmg.wshammeron.constants;

public abstract class KeyMessageConstants {
    public static final String AUTHORIZATION_REQUIRED = "authorization.required";
    public static final String ARGUMENTS_INVALID = "arguments.invalid";
    public static final String DATA_INVALID = "data.invalid";
    public static final String DATA_FIELDS_INVALID = "data.fields.invalid";

    public static final String ERROR_500_DEFAULT = "error.500.default";
    public static final String ERROR_409_DEFAULT = "error.409.default";
    public static final String ERROR_404_DEFAULT = "error.404.default";
    public static final String ERROR_403_DEFAULT = "error.403.default";
    public static final String ERROR_401_DEFAULT = "error.401.default";
    public static final String ERROR_422_DEFAULT = "error.422.default";

    public static final String TOKEN_JWT_INVALID = "token.jwt.invalid";
    public static final String TOKEN_ACTIVATE_EXPIRED = "token.activate.expired";
    public static final String TOKEN_ACTIVATE_FAILED_SEND = "token.activate.failed.send";

    /* User */
    public static final String USER_OR_PASSWORD_INCORRECT = "user.password.incorrect";
    public static final String USER_PASSWORD_EXPIRED = "user.password.expired";
    public static final String USER_ACTIVATE_ACCOUNT = "user.activate.account";
    public static final String USER_ALREADY_EXISTS = "user.already.exists";
    public static final String USER_HAS_PERMISSION = "space.user.email.permission.already.exists";
    public static final String USER_DOES_NOT_PERMISSION_TO_ACTION = "user.without.permission.to.action";

    /* Space */

    public static final String SPACE_ALREADY_EXIST = "space.already.exist";
    public static final String SPACE_NOT_EXIST = "space.not.exist";
    public static final String SPACE_USER_NOT_EXIST = "space.user.not.access";
    public static final String SPACE_APPROVE_NOT_FOUND_REQUESTED = "space.approve.notFound.requested";

    /* Music */
    public static final String MUSIC_INVALID_LINK = "music.invalid.link";
    public static final String MUSIC_NOT_EXIST_SPACE = "music.not.exist.space";
    public static final String MUSIC_ALREADY_EXIST_SPACE = "music.already.exist.space";
    public static final String MUSIC_CANNOT_CHANGE_STATUS = "music.cannot.change.status";

    /* Event */

    public static final String EVENT_ALREADY_EXIST = "event.already.exist";
    public static final String EVENT_CREATE_DATETIME_INVALID = "event.create.datetime.invalid";
    public static final String EVENT_DATE_RANGE_REQUIRED = "event.dateRange.required";
    public static final String EVENT_IS_NOT_EDITABLE = "event.not.editable";
    public static final String EVENT_DO_NOT_EXIST = "event.not.exist";
    public static final String EVENT_DO_NOT_EXIST_ON_SPACE = "event.not.exist.space";
    public static final String EVENT_USER_LIST_INVALID = "event.user.list.invalid";
    public static final String EVENT_SMS_NEW_OR_UPDATE = "event.sms.new.association";
    public static final String EVENT_SMS_DELETE = "event.sms.remove.association";
}
