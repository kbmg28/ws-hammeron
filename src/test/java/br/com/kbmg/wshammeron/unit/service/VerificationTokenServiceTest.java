package br.com.kbmg.wshammeron.unit.service;

import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.repository.VerificationTokenRepository;
import br.com.kbmg.wshammeron.service.impl.VerificationTokenServiceImpl;
import br.com.kbmg.wshammeron.unit.BaseUnitTests;
import builder.UserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;

class VerificationTokenServiceTest extends BaseUnitTests {

    @InjectMocks
    private VerificationTokenServiceImpl verificationTokenService;

    @Mock
    private VerificationTokenRepository repositoryMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteByUserApp_shouldDelete() {
        UserApp userApp = UserBuilder.generateUserAppLogged();

        verificationTokenService.deleteByUserApp(userApp);

        assertAll(
                () -> verify(repositoryMock).deleteByUserApp(userApp)
        );
    }

}
