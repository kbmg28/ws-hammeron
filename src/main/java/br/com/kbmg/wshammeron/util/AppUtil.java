package br.com.kbmg.wshammeron.util;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordValidator;
import org.passay.RepeatCharacterRegexRule;
import org.passay.WhitespaceRule;

import java.util.Arrays;

public abstract class AppUtil {

    public static PasswordValidator getPasswordValidatorPattern() {
        return new PasswordValidator(Arrays.asList(
                new CharacterRule(EnglishCharacterData.LowerCase),
                new CharacterRule(EnglishCharacterData.UpperCase),
                new CharacterRule(EnglishCharacterData.Digit),
                new LengthRule(6, 20),
                new RepeatCharacterRegexRule(3),
                new WhitespaceRule()));
    }
}
