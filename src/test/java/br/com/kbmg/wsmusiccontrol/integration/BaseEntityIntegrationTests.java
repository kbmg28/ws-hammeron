package br.com.kbmg.wsmusiccontrol.integration;

import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.dto.user.ActivateUserAccountRefreshDto;
import br.com.kbmg.wsmusiccontrol.dto.user.LoginDto;
import br.com.kbmg.wsmusiccontrol.dto.user.RegisterPasswordDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserTokenHashDto;
import br.com.kbmg.wsmusiccontrol.model.AbstractEntity;
import br.com.kbmg.wsmusiccontrol.model.Music;
import br.com.kbmg.wsmusiccontrol.model.Singer;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.SpaceUserAppAssociation;
import br.com.kbmg.wsmusiccontrol.model.VerificationToken;
import br.com.kbmg.wsmusiccontrol.repository.MusicRepository;
import br.com.kbmg.wsmusiccontrol.repository.SingerRepository;
import br.com.kbmg.wsmusiccontrol.repository.SpaceRepository;
import br.com.kbmg.wsmusiccontrol.repository.SpaceUserAppAssociationRepository;
import br.com.kbmg.wsmusiccontrol.repository.VerificationTokenRepository;
import builder.MusicBuilder;
import builder.SpaceBuilder;
import builder.UserBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Properties;
import java.util.UUID;

import static constants.BaseTestsConstants.AUTHENTICATED_USER_TEST_EMAIL;
import static constants.BaseTestsConstants.AUTHENTICATED_USER_TEST_PASSWORD;
import static org.mockito.Mockito.when;

public abstract class BaseEntityIntegrationTests extends BaseIntegrationTests{

    /* DTO */
    protected LoginDto loginDtoTest;
    protected UserDto userDtoTest;
    protected RegisterPasswordDto registerPasswordDtoTest;
    protected UserTokenHashDto userTokenHashDtoTest;
    protected ActivateUserAccountRefreshDto activateUserAccountRefreshDtoTest;
    protected MusicWithSingerAndLinksDto musicWithSingerAndLinksDtoTest;

    /* Entities */
    protected Space spaceTest;
    protected SpaceUserAppAssociation spaceUserAppAssociationTest;
    protected Music musicTest;
    protected Singer singerTest;

    /* Injects */
    @Nullable
    protected Session session;

    @Autowired
    protected VerificationTokenRepository verificationTokenRepository;

    @Autowired
    protected SpaceRepository spaceRepository;

    @Autowired
    protected SpaceUserAppAssociationRepository spaceUserAppAssociationRepository;

    @Autowired
    protected MusicRepository musicRepository;

    @Autowired
    protected SingerRepository singerRepository;

    /* TESTS */

    protected void givenMimeMessage() {
        Properties javaMailProperties = new Properties();
        if (this.session == null) {
            this.session = Session.getInstance(javaMailProperties);
        }

        MimeMessage mimeMessageTest = new MimeMessage(session);

        when(mailSenderMockBean.createMimeMessage()).thenReturn(mimeMessageTest);
    }

    protected void givenUserLoggedNotEnabled() {
        userAppLoggedTest.setEnabled(false);
        userAppRepository.save(userAppLoggedTest);
    }

    protected void givenUserDto() {
        userDtoTest = UserBuilder.generateUserDto();
    }

    protected void givenRegisterPasswordDto() {
        registerPasswordDtoTest = UserBuilder.generateRegisterPasswordDto();
    }

    protected void givenActivateUserAccountRefreshDto() {
        activateUserAccountRefreshDtoTest = UserBuilder.generateActivateUserAccountRefreshDto();
    }

    protected void givenUserTokenHashDto() {
        userTokenHashDtoTest = UserBuilder.generateUserTokenHashDto();
    }

    protected void givenLoginDto() {
        loginDtoTest = UserBuilder.generateLoginDto(AUTHENTICATED_USER_TEST_EMAIL, AUTHENTICATED_USER_TEST_PASSWORD);
    }

    protected void givenLoginDto(String email, String pass) {
        loginDtoTest = UserBuilder.generateLoginDto(email, pass);
    }

    protected void givenMusicWithSingerAndLinksDto() {
        musicWithSingerAndLinksDtoTest = MusicBuilder.generateMusicWithSingerAndLinksDto();
    }

    protected void givenVerificationToken() {
        VerificationToken verificationTokenTest = UserBuilder.generateVerificationToken(userAppLoggedTest);
        verificationTokenRepository.save(verificationTokenTest);
    }

    protected void givenVerificationTokenExpired() {
        VerificationToken verificationTokenTest = UserBuilder.generateVerificationToken(userAppLoggedTest);
        verificationTokenRepository.save(verificationTokenTest);

        LocalDateTime localDateTime = verificationTokenTest.getExpiryDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDateTime expiredTime = localDateTime.minusMinutes(11);

        verificationTokenTest.setExpiryDate(Timestamp.valueOf(expiredTime));

        verificationTokenRepository.save(verificationTokenTest);
    }

    protected void givenSpaceInDatabase() {
        spaceTest = SpaceBuilder.generateSpace(userAppLoggedTest);
        spaceRepository.save(spaceTest);
    }

    protected void givenSpaceUserAppAssociationInDatabase(Boolean isOwner) {
        spaceUserAppAssociationTest = SpaceBuilder.generateSpaceUserAppAssociation(spaceTest, userAppLoggedTest, isOwner);
        spaceUserAppAssociationRepository.save(spaceUserAppAssociationTest);
    }

    protected void givenMusicInDatabase() {
        singerTest = MusicBuilder.generateSinger();
        singerRepository.save(singerTest);

        musicTest = MusicBuilder.generateMusic(spaceTest, singerTest);
        musicRepository.save(musicTest);
    }

    protected <T extends AbstractEntity> String getIdOrOtherInvalid(T entity) {
        return entity == null ? UUID.randomUUID().toString() : entity.getId();
    }

}
