package builder;

import br.com.kbmg.wshammeron.config.recaptcha.v3.GoogleResponse;

public abstract class TokenBuilder {

    public static GoogleResponse generateGoogleResponse() {

        GoogleResponse googleResponse = new GoogleResponse();

        googleResponse.setSuccess(true);

        return googleResponse;
    }

}
