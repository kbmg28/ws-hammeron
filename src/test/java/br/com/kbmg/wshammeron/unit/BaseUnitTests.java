package br.com.kbmg.wshammeron.unit;


import br.com.kbmg.wshammeron.config.logging.LogService;
import br.com.kbmg.wshammeron.config.messages.MessagesService;
import br.com.kbmg.wshammeron.constants.JwtConstants;
import br.com.kbmg.wshammeron.dto.music.MusicDto;
import br.com.kbmg.wshammeron.dto.music.MusicLinkDto;
import br.com.kbmg.wshammeron.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wshammeron.dto.music.SingerDto;
import br.com.kbmg.wshammeron.model.Music;
import br.com.kbmg.wshammeron.model.MusicLink;
import br.com.kbmg.wshammeron.model.Singer;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.SpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.service.EventMusicAssociationService;
import br.com.kbmg.wshammeron.service.EventService;
import br.com.kbmg.wshammeron.service.EventSpaceUserAppAssociationService;
import br.com.kbmg.wshammeron.service.JwtService;
import br.com.kbmg.wshammeron.service.MusicLinkService;
import br.com.kbmg.wshammeron.service.MusicService;
import br.com.kbmg.wshammeron.service.SingerService;
import br.com.kbmg.wshammeron.service.SpaceService;
import br.com.kbmg.wshammeron.service.SpaceUserAppAssociationService;
import br.com.kbmg.wshammeron.service.UserAppService;
import br.com.kbmg.wshammeron.service.UserPermissionService;
import br.com.kbmg.wshammeron.service.VerificationTokenService;
import br.com.kbmg.wshammeron.util.mapper.MusicMapper;
import br.com.kbmg.wshammeron.util.mapper.OverviewMapper;
import br.com.kbmg.wshammeron.util.mapper.UserAppMapper;
import builder.SpaceBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static builder.MusicBuilder.generateMusic;
import static builder.MusicBuilder.generateMusicLinks;
import static builder.MusicBuilder.generateSinger;
import static builder.SpaceBuilder.generateSpace;
import static builder.UserBuilder.generateSpaceUserAppAssociation;
import static builder.UserBuilder.generateUserAppLogged;
import static constants.BaseTestsConstants.SECRET_UNIT_TEST;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@Tag("unitTest")
@ContextConfiguration
@WithMockUser(roles = "PARTICIPANT")
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

    @Mock
    protected MusicMapper musicMapperMock;

    @Mock
    protected SingerService singerServiceMock;

    @Mock
    protected MusicLinkService musicLinkServiceMock;

    @Mock
    protected EventMusicAssociationService eventMusicAssociationServiceMock;

    @Mock
    protected OverviewMapper overviewMapperMock;

    @Mock
    protected UserAppService userAppServiceMock;

    @Mock
    protected JwtService jwtServiceMock;

    @Mock
    protected ApplicationEventPublisher eventPublisherMock;

    @Mock
    protected MusicService musicServiceMock;

    @Mock
    protected EventService eventServiceMock;

    protected UserApp givenUserAppFull() {
        UserApp userApp = generateUserAppLogged();
        userApp.setId(UUID.randomUUID().toString());

        Space space = SpaceBuilder.generateSpace(userApp);
        space.setId(UUID.randomUUID().toString());

        generateSpaceUserAppAssociation(userApp, space);
        
        return userApp;
    }

    protected MusicDto givenMusicDto(Music music) {
        SingerDto singerDto = givenSingerDto(music.getSinger());
        Set<MusicLinkDto> musicLinkDtoList = givenMusicLinkDtoList(music);

        return new MusicDto(
                music.getId(),
                music.getName(),
                music.getMusicStatus(),
                singerDto,
                musicLinkDtoList,
                null
        );
    }

    protected MusicWithSingerAndLinksDto givenMusicWithSingerAndLinksDto(Music music) {
        Singer singer = music.getSinger();
        SingerDto singerDto = givenSingerDto(singer);

        Set<MusicLinkDto> musicLinkDtoList = givenMusicLinkDtoList(music);

        return new MusicWithSingerAndLinksDto(null, music.getName(),
                music.getMusicStatus(), singerDto, musicLinkDtoList);
    }

    protected Set<MusicLinkDto> givenMusicLinkDtoList(Music music) {
        Set<MusicLinkDto> musicLinkDtoList = music
                .getMusicLinkList()
                .stream()
                .map(musicLink -> new MusicLinkDto(musicLink.getId(), musicLink.getLink(), musicLink.getTypeLink()))
                .collect(Collectors.toSet());
        return musicLinkDtoList;
    }

    protected SingerDto givenSingerDto(Singer singer) {
        return new SingerDto(singer.getId(), singer.getName());
    }

    protected Music givenMusicFull() {
        UserApp userApp = generateUserAppLogged();

        Space space = generateSpace(userApp);
        space.setId(UUID.randomUUID().toString());

        Singer singer = generateSinger();
        singer.setId(UUID.randomUUID().toString());

        Music music = generateMusic(space, singer);
        music.setId(UUID.randomUUID().toString());

        Set<MusicLink> musicLinks = generateMusicLinks(music);
        musicLinks.forEach(musicLink -> musicLink.setId(UUID.randomUUID().toString()));
        music.getMusicLinkList().addAll(musicLinks);
        return music;
    }

    protected String givenValidJwt(UserApp userApp) {
        return generateJwt(600000L, userApp);
    }

    protected String generateJwt(Long plusExpiration, UserApp userApp) {
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

    protected Space givenSpace() {
        UserApp userApp = givenUserAppFull();

        return givenSpace(userApp);
    }

    protected Space givenSpace(UserApp userApp) {
        return userApp
                .getSpaceUserAppAssociationList()
                .stream()
                .findFirst()
                .map(SpaceUserAppAssociation::getSpace)
                .orElseThrow();
    }

}
