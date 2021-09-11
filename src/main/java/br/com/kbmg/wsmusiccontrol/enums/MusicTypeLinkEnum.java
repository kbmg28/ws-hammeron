package br.com.kbmg.wsmusiccontrol.enums;

import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import lombok.Getter;

import java.util.regex.Pattern;

public enum MusicTypeLinkEnum {
    YOUTUBE("^((?:https?:)?\\/\\/)?((?:www|m)\\.)?((?:youtube\\.com|youtu.be))(\\/(?:[\\w\\-]+\\?v=|embed\\/|v\\/)?)([\\w\\-]+)(\\S+)?$"),
    SPOTIFY("^(https:\\/\\/open.spotify.com\\/)([a-zA-Z0-9]+)(.*)$"),
    CHORD  ("^((?:https?:)?\\/\\/)?((?:www|m)\\.)?(cifraclub.com.br\\/)([a-zA-Z0-9]+)(.*)$");

    @Getter
    private final String regex;

    MusicTypeLinkEnum(String regex) {
        this.regex = regex;
    }

    public void validateUrl(String link) {
        Pattern pattern = Pattern.compile(this.getRegex(), Pattern.CASE_INSENSITIVE);
        boolean isValid = pattern.asPredicate().test(link);

        if (!isValid) {
            throw new ServiceException("invalid link");
        }
    }

}
