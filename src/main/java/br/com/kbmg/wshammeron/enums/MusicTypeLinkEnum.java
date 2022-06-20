package br.com.kbmg.wshammeron.enums;

import lombok.Getter;

import java.util.regex.Pattern;

public enum MusicTypeLinkEnum {
    YOUTUBE("YouTube", "^((?:https?:)?\\/\\/)?((?:www|m)\\.)?((?:youtube\\.com|youtu.be))(\\/(?:[\\w\\-]+\\?v=|embed\\/|v\\/)?)([\\w\\-]+)(\\S+)?$"),
    SPOTIFY("Spotify", "^(https:\\/\\/open.spotify.com\\/)([a-zA-Z0-9]+)(.*)$"),
    CHORD  ("Cifra Club", "^((?:https?:)?\\/\\/)?((?:www|m)\\.)?(cifraclub.com.br\\/)([a-zA-Z0-9]+)(.*)$");

    @Getter
    private final String regex;

    @Getter
    private final String name;

    MusicTypeLinkEnum(String name, String regex) {
        this.name = name;
        this.regex = regex;
    }

    public boolean validateUrl(String link) {
        Pattern pattern = Pattern.compile(this.getRegex(), Pattern.CASE_INSENSITIVE);
        boolean isValid = pattern.asPredicate().test(link);

        return !isValid;
    }

}
