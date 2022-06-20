package br.com.kbmg.wshammeron.service.impl;

import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.model.VerificationToken;
import br.com.kbmg.wshammeron.repository.VerificationTokenRepository;
import br.com.kbmg.wshammeron.service.VerificationTokenService;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenServiceImpl
        extends GenericServiceImpl<VerificationToken, VerificationTokenRepository>
        implements VerificationTokenService {

    @Override
    public void deleteByUserApp(UserApp userApp) {
        repository.deleteByUserApp(userApp);
    }
}
