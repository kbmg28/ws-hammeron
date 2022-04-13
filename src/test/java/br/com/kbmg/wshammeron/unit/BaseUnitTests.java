package br.com.kbmg.wshammeron.unit;


import br.com.kbmg.wshammeron.config.logging.LogService;
import br.com.kbmg.wshammeron.config.messages.MessagesService;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.service.EventSpaceUserAppAssociationService;
import br.com.kbmg.wshammeron.service.SpaceService;
import br.com.kbmg.wshammeron.service.SpaceUserAppAssociationService;
import br.com.kbmg.wshammeron.service.UserPermissionService;
import br.com.kbmg.wshammeron.service.VerificationTokenService;
import br.com.kbmg.wshammeron.util.mapper.UserAppMapper;
import builder.SpaceBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;

import static builder.UserBuilder.generateSpaceUserAppAssociation;
import static builder.UserBuilder.generateUserAppLogged;

@ExtendWith(MockitoExtension.class)
@Tag("unitTest")
public abstract class BaseUnitTests {

    @Mock
    protected EntityManager entityManagerMock;

    @Mock
    protected LogService logServiceMock;

    @Mock
    protected MessagesService messagesServiceMock;

    @Mock
    protected UserPermissionService userPermissionServiceMock;

    @Mock
    protected SpaceService spaceServiceMock;

    @Mock
    protected EventSpaceUserAppAssociationService eventSpaceUserAppAssociationServiceMock;

    @Mock
    protected SpaceUserAppAssociationService spaceUserAppAssociationServiceMock;

    @Mock
    protected UserAppMapper userAppMapperMock;

    @Mock
    protected VerificationTokenService verificationTokenServiceMock;

    protected UserApp givenUserAppFull() {
        UserApp userApp = generateUserAppLogged();
        Space space = SpaceBuilder.generateSpace(userApp);
        generateSpaceUserAppAssociation(userApp, space);
        return userApp;
    }

}
