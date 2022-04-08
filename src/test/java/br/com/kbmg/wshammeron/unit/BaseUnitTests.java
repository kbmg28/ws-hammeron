package br.com.kbmg.wshammeron.unit;


import br.com.kbmg.wshammeron.config.logging.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
@Tag("unitTest")
public abstract class BaseUnitTests {

    @Mock
    protected EntityManager entityManager;

    @Mock
    protected LogService logService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

}
