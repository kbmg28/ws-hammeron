package br.com.kbmg.wshammeron.unit.service;

import br.com.kbmg.wshammeron.constants.JwtConstants;
import br.com.kbmg.wshammeron.dto.user.LoginDto;
import br.com.kbmg.wshammeron.enums.PermissionEnum;
import br.com.kbmg.wshammeron.exception.AuthorizationException;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.SpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.repository.VerificationTokenRepository;
import br.com.kbmg.wshammeron.service.impl.JwtServiceImpl;
import br.com.kbmg.wshammeron.unit.BaseUnitTests;
import builder.UserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static br.com.kbmg.wshammeron.unit.ExceptionAssertions.thenShouldThrowException;
import static constants.BaseTestsConstants.SECRET_UNIT_TEST;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtServiceTest extends BaseUnitTests {

    @InjectMocks
    private JwtServiceImpl jwtService;

    @Mock
    private VerificationTokenRepository repositoryMock;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(jwtService, "expiration", "600000");
        ReflectionTestUtils.setField(jwtService, "secret", SECRET_UNIT_TEST);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateToken_shouldCreateNewJwtToken() {
        UserApp userApp = givenUserAppFull();
        SpaceUserAppAssociation spaceUserAppAssociation = userApp.getSpaceUserAppAssociationList().stream().findFirst().orElseThrow();
        Space space = spaceUserAppAssociation.getSpace();
        LoginDto loginDto = UserBuilder.generateLoginDto();

        String result = jwtService.generateToken(loginDto, userApp, space);
        assertJwtValid(result);
    }

    @Test
    void isValidToken_shouldReturnTrue() {
        UserApp userApp = givenUserAppFull();
        String jwt = givenValidJwt(userApp);

        boolean result = jwtService.isValidToken(jwt);

        assertTrue(result);
    }

    @Test
    void isValidToken_shouldReturnFalse() {
        UserApp userApp = givenUserAppFull();
        String jwt = givenInvalidJwt(userApp);

        boolean result = jwtService.isValidToken(jwt);

        assertFalse(result);
    }

    @Test
    void validateTokenAndGetUserId_shouldReturnUserId() {
        UserApp userApp = givenUserAppFull();
        String jwt = givenValidJwt(userApp);

        String result = jwtService.validateTokenAndGetUserId(jwt);

        assertEquals(userApp.getId(), result);
    }

    @Test
    void validateTokenAndGetUserId_shouldReturnAuthorizationException() {
        UserApp userApp = givenUserAppFull();
        String jwt = givenInvalidJwt(userApp);

        thenShouldThrowException(AuthorizationException.class, jwt, jwtService::validateTokenAndGetUserId);
    }

    @Test
    void getValue_shouldReturnEmail() {
        UserApp userApp = givenUserAppFull();
        String jwt = givenValidJwt(userApp);

        String result = jwtService.getValue(jwt, JwtConstants.CLAIM_EMAIL);

        assertEquals(userApp.getEmail(), result);
    }

    @Test
    void updateSpaceOnToken_shouldReturnJwtUpdated() {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        UserApp userApp = givenUserAppFull();
        SpaceUserAppAssociation spaceUserAppAssociation = userApp.getSpaceUserAppAssociationList().stream().findFirst().orElseThrow();
        Space space = spaceUserAppAssociation.getSpace();
        String jwt = givenValidJwt(userApp);

        when(mockedRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(JwtConstants.BEARER.concat(jwt));
        when(userAppServiceMock.findByIdValidated(userApp.getId(), "")).thenReturn(userApp);
        when(userPermissionServiceMock.findAllBySpaceAndUserApp(any(), any())).thenReturn(List.of(PermissionEnum.PARTICIPANT.name()));

        String result = jwtService.updateSpaceOnToken(mockedRequest, new Space());

        assertJwtValid(result);
    }

    private void assertJwtValid(String result) {
        String startOfTokenResult = result.substring(0, 7);
        String tokenResult = result.substring(7);

        assertAll(
                () -> verify(userPermissionServiceMock).findAllBySpaceAndUserApp(any(), any()),
                () -> assertDoesNotThrow(() -> Jwts.parser().setSigningKey(SECRET_UNIT_TEST).parseClaimsJws(tokenResult)),
                () -> assertEquals(JwtConstants.BEARER, startOfTokenResult)
        );
    }

    private String givenValidJwt(UserApp userApp) {
        return generateJwt(600000L, userApp);
    }

    private String givenInvalidJwt(UserApp userApp) {
        return generateJwt(1L, userApp);
    }

    private String generateJwt(Long plusExpiration, UserApp userApp) {
        Date startDate = new Date();
        Date expirationDate = new Date(startDate.getTime() + plusExpiration);

        return Jwts.builder()
                .setSubject(userApp.getId())
                .setIssuedAt(startDate)
                .claim(JwtConstants.CLAIM_EMAIL, userApp.getEmail())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_UNIT_TEST)
                .compact();
    }

}
