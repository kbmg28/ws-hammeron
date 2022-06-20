package br.com.kbmg.wshammeron.integration;

import br.com.kbmg.wshammeron.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wshammeron.dto.user.ActivateUserAccountRefreshDto;
import br.com.kbmg.wshammeron.dto.user.LoginDto;
import br.com.kbmg.wshammeron.dto.user.RegisterPasswordDto;
import br.com.kbmg.wshammeron.dto.user.UserChangePasswordDto;
import br.com.kbmg.wshammeron.dto.user.UserDto;
import br.com.kbmg.wshammeron.dto.user.UserTokenHashDto;
import br.com.kbmg.wshammeron.enums.PermissionEnum;
import br.com.kbmg.wshammeron.enums.SpaceStatusEnum;
import br.com.kbmg.wshammeron.model.AbstractEntity;
import br.com.kbmg.wshammeron.model.Music;
import br.com.kbmg.wshammeron.model.Singer;
import br.com.kbmg.wshammeron.model.SpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.UserPermission;
import br.com.kbmg.wshammeron.model.VerificationToken;
import br.com.kbmg.wshammeron.repository.MusicRepository;
import br.com.kbmg.wshammeron.repository.SingerRepository;
import br.com.kbmg.wshammeron.repository.SpaceRepository;
import br.com.kbmg.wshammeron.repository.VerificationTokenRepository;
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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static constants.BaseTestsConstants.ANY_VALUE;
import static constants.BaseTestsConstants.USER_TEST_PASSWORD;
import static constants.BaseTestsConstants.generateRandomEmail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class BaseEntityIntegrationTests extends BaseIntegrationTests{

    /* DTO */
    protected LoginDto loginDtoTest;
    protected UserDto userDtoTest;
    protected RegisterPasswordDto registerPasswordDtoTest;
    protected UserTokenHashDto userTokenHashDtoTest;
    protected ActivateUserAccountRefreshDto activateUserAccountRefreshDtoTest;
    protected UserChangePasswordDto userChangePasswordDtoTest;
    protected MusicWithSingerAndLinksDto musicWithSingerAndLinksDtoTest;

    /* Entities */
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
        userDtoTest = UserBuilder.generateUserDto(generateRandomEmail());
    }

    protected UserDto givenUserDto(String email) {
        return UserBuilder.generateUserDto(email);
    }

    protected void givenRegisterPasswordDto(String email) {
        registerPasswordDtoTest = UserBuilder.generateRegisterPasswordDto(email);
    }

    protected void givenActivateUserAccountRefreshDto(String email) {
        activateUserAccountRefreshDtoTest = UserBuilder.generateActivateUserAccountRefreshDto(email);
    }

    protected void givenUserChangePasswordDto() {
        userChangePasswordDtoTest = UserBuilder.generateUserChangePasswordDto(userAppLoggedTest);
    }

    protected void givenUserTokenHashDto(String email) {
        userTokenHashDtoTest = UserBuilder.generateUserTokenHashDto(email);
    }

    protected void givenLoginDto() {
        loginDtoTest = UserBuilder.generateLoginDto(userAppLoggedTest.getEmail(), USER_TEST_PASSWORD);
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

    protected void givenSpaceUserAppAssociationOwnerInDatabase() {
        givenSpaceUserAppAssociationInDatabase(PermissionEnum.SPACE_OWNER);
    }

    protected void givenSpaceUserAppAssociationParticipantInDatabase() {
        givenSpaceUserAppAssociationInDatabase(PermissionEnum.PARTICIPANT);
    }

    protected void givenSpaceWithStatus(SpaceStatusEnum spaceStatusEnum) {
        if(SpaceStatusEnum.REQUESTED.equals(spaceStatusEnum)) {
            spaceTest.setApprovedBy(null);
            spaceTest.setApprovedByDate(null);
        }
        spaceTest.setSpaceStatus(spaceStatusEnum);
    }

    protected void givenSpaceUserAppAssociationInDatabase(PermissionEnum... permissions) {
        Set<UserPermission> userPermissionList = Stream.of(permissions).map(perm -> {
            UserPermission userPermission = new UserPermission();

            userPermission.setPermission(perm);

            return userPermission;
        }).collect(Collectors.toSet());

        spaceUserAppAssociationTest = SpaceBuilder.generateSpaceUserAppAssociation(spaceTest, userAppLoggedTest, userPermissionList);
        spaceUserAppAssociationRepository.save(spaceUserAppAssociationTest);
    }

    protected void givenSpaceRequestDto() {
        givenSpaceRequestDto(ANY_VALUE.concat(UUID.randomUUID().toString()));
    }

    protected void givenSpaceRequestDto(String spaceName) {
        spaceRequestDtoTest = SpaceBuilder.generateSpaceRequestDto(spaceTest);
        spaceRequestDtoTest.setName(spaceName);
    }

    protected void givenSpaceApproveDto() {
        spaceApproveDtoTest = SpaceBuilder.generateSpaceApproveDto();
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

    protected void thenShouldReturnContentEmpty() throws Exception {
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").doesNotExist())
        ;
    }
}
