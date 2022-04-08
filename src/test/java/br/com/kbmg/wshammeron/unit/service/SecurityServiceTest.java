//package br.com.kbmg.wshammeron.unit.service;
//
//import br.com.kbmg.wshammeron.config.messages.MessagesService;
//import br.com.kbmg.wshammeron.repository.VerificationTokenRepository;
//import br.com.kbmg.wshammeron.service.JwtService;
//import br.com.kbmg.wshammeron.service.UserAppService;
//import br.com.kbmg.wshammeron.service.impl.SecurityServiceImpl;
//import br.com.kbmg.wshammeron.unit.BaseUnitTests;
//import builder.UserBuilder;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.context.ApplicationEventPublisher;
//
//import static org.mockito.Mockito.when;
//
//public class SecurityServiceTest extends BaseUnitTests {
//
//    @InjectMocks
//    private SecurityServiceImpl securityService;
//
//    @Mock
//    private VerificationTokenRepository tokenRepository;
//
//    @Mock
//    private UserAppService userAppService;
//
//    @Mock
//    private JwtService jwtService;
//
//    @Mock
//    private ApplicationEventPublisher eventPublisher;
//
//    @Mock
//    public MessagesService messagesService;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void validateLoginAndGetToken_shouldReturn() {
//        givenUserApp();
//        whenCalling_UserAppService_findByEmail_shouldReturnUserApp();
//        whenCalled_validateLoginAndGetToken();
//    }
//
//    private void givenUserApp() {
//        userAppTest = UserBuilder.generateUserAppLogged()
//    }
//
//    private void whenCalling_UserAppService_findByEmail_shouldReturnUserApp() {
//        when(userAppService.findByEmail()).thenReturn();
//    }
//
//    private void whenCalled_validateLoginAndGetToken() {
////        securityService.validateLoginAndGetToken()
//    }
//
//}
