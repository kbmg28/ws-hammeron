package br.com.kbmg.wshammeron.unit.service;

import br.com.kbmg.wshammeron.repository.UserAppRepository;
import br.com.kbmg.wshammeron.service.impl.UserAppServiceImpl;
import br.com.kbmg.wshammeron.unit.BaseUnitTests;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserAppServiceTest extends BaseUnitTests {

    @InjectMocks
    private UserAppServiceImpl userAppService;

    @Mock
    private UserAppRepository userAppRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

}
