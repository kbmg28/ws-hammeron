package br.com.kbmg.wsmusiccontrol.unit.service;

import br.com.kbmg.wsmusiccontrol.config.messages.MessagesService;
import br.com.kbmg.wsmusiccontrol.repository.VerificationTokenRepository;
import br.com.kbmg.wsmusiccontrol.service.JwtService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import br.com.kbmg.wsmusiccontrol.service.impl.SecurityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import br.com.kbmg.wsmusiccontrol.unit.BaseUnitTests;

public class SecurityServiceTest extends BaseUnitTests {

    @InjectMocks
    private SecurityServiceImpl securityService;

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private UserAppService userAppService;

    @Mock
    private JwtService jwtService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    public MessagesService messagesService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

}
