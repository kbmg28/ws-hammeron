package br.com.kbmg.wsmusiccontrol.unit.service;

import br.com.kbmg.wsmusiccontrol.repository.UserAppRepository;
import br.com.kbmg.wsmusiccontrol.service.impl.UserAppServiceImpl;
import br.com.kbmg.wsmusiccontrol.unit.BaseUnitTests;
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
