package br.com.kbmg.wshammeron.service;

import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.model.VerificationToken;

public interface VerificationTokenService extends GenericService<VerificationToken>{

    void deleteByUserApp(UserApp userApp);
}
