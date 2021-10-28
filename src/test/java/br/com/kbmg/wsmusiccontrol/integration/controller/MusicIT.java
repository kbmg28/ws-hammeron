package br.com.kbmg.wsmusiccontrol.integration.controller;

import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.integration.BaseEntityIntegrationTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.ERROR_403_DEFAULT;
import static br.com.kbmg.wsmusiccontrol.integration.ResponseErrorExpect.thenReturnHttpError400_BadRequest;
import static br.com.kbmg.wsmusiccontrol.integration.ResponseErrorExpect.thenReturnHttpError403_ForbiddenWithPermission;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MusicIT extends BaseEntityIntegrationTests {

    private static final String URL_TEMPLATE = "/api/spaces/{space-id}/musics";
    private static final String PATH_PARAM_SPACE_ID = "{space-id}";

    @BeforeEach
    public void before() {
        super.beforeAllTestsBase();
        givenUserAuthenticatedWithPermission(PermissionEnum.PARTICIPANT);
    }

    @Test
    public void findAllMusic_shouldReturnMusicWithSingerAndLinksDtoList() throws Exception {
        givenSpaceInDatabase();
        givenSpaceUserAppAssociationInDatabase(false);
        givenMusicInDatabase();
        whenRequestGetFindAllMusic();
        thenShouldReturnList();
    }

    @Test
    public void findAllMusic_shouldReturnErrorIfUserDoesNotBelongToSpace() throws Exception {
        givenSpaceInDatabase();
        whenRequestGetFindAllMusic();
        thenReturnHttpError403_ForbiddenWithPermission(PermissionEnum.PARTICIPANT, perform, messagesService.get("space.user.not.access"));
    }

    @Test
    public void findById_shouldReturnMusicWithSingerAndLinksDto() throws Exception {
        givenSpaceInDatabase();
        givenSpaceUserAppAssociationInDatabase(false);
        givenMusicInDatabase();
        whenRequestGetFindById();
        thenShouldReturnMusicWithSingerAndLinksDto();
    }

    @Test
    public void findById_shouldReturnErrorIfMusicNotExistInSpace() throws Exception {
        givenSpaceInDatabase();
        givenSpaceUserAppAssociationInDatabase(false);
        whenRequestGetFindById();
        thenReturnHttpError400_BadRequest(perform, messagesService.get("music.not.exist.space"));
    }

    @Test
    public void findById_shouldReturnErrorIfUserDoesNotBelongToSpace() throws Exception {
        givenSpaceInDatabase();
        whenRequestGetFindById();
        thenReturnHttpError403_ForbiddenWithPermission(PermissionEnum.PARTICIPANT, perform, messagesService.get("space.user.not.access"));
    }

    @Test
    public void findAllSinger_shouldReturnSingerDtoList() throws Exception {
        givenSpaceInDatabase();
        givenSpaceUserAppAssociationInDatabase(false);
        givenMusicInDatabase();
        whenRequestGetFindAllSinger();
        thenShouldReturnList();
    }

    @Test
    public void findAllSinger_shouldReturnErrorIfUserDoesNotBelongToSpace() throws Exception {
        givenSpaceInDatabase();
        whenRequestGetFindAllSinger();
        thenReturnHttpError403_ForbiddenWithPermission(PermissionEnum.PARTICIPANT, perform, messagesService.get("space.user.not.access"));
    }

    @Test
    public void createMusic_shouldReturnMusicWithSingerAndLinksDto() throws Exception {
        givenSpaceInDatabase();
        givenSpaceUserAppAssociationInDatabase(false);
        givenMusicWithSingerAndLinksDto();
        whenRequestPostCreateMusic();
        thenShouldReturnMusicWithSingerAndLinksDto();
    }

    @Test
    public void createMusic_shouldReturnErrorIfMusicAlreadyExistInSpace() throws Exception {
        givenSpaceInDatabase();
        givenSpaceUserAppAssociationInDatabase(false);
        givenMusicInDatabase();
        givenMusicWithSingerAndLinksDto();
        whenRequestPostCreateMusic();
        thenReturnHttpError400_BadRequest(perform, messagesService.get("music.already.exist.space"));
    }

    @Test
    public void createMusic_shouldReturnErrorIfUserDoesNotBelongToSpace() throws Exception {
        givenSpaceInDatabase();
        givenMusicWithSingerAndLinksDto();
        whenRequestPostCreateMusic();
        thenReturnHttpError403_ForbiddenWithPermission(PermissionEnum.PARTICIPANT, perform, messagesService.get("space.user.not.access"));
    }

    @Test
    public void updateMusic_shouldReturnMusicWithSingerAndLinksDto() throws Exception {
        givenSpaceInDatabase();
        givenSpaceUserAppAssociationInDatabase(false);
        givenMusicWithSingerAndLinksDto();
        givenMusicInDatabase();
        whenRequestPutUpdateMusic();
        thenShouldReturnMusicWithSingerAndLinksDto();
    }

    @Test
    public void updateMusic_shouldReturnErrorIfMusicNotExistInSpace() throws Exception {
        givenSpaceInDatabase();
        givenSpaceUserAppAssociationInDatabase(false);
        givenMusicWithSingerAndLinksDto();
        whenRequestPutUpdateMusic();
        thenReturnHttpError400_BadRequest(perform, messagesService.get("music.not.exist.space"));
    }

    @Test
    public void updateMusic_shouldReturnErrorIfUserDoesNotBelongToSpace() throws Exception {
        givenSpaceInDatabase();
        givenMusicWithSingerAndLinksDto();
        whenRequestPutUpdateMusic();
        thenReturnHttpError403_ForbiddenWithPermission(PermissionEnum.PARTICIPANT, perform, messagesService.get("space.user.not.access"));
    }

    @Test
    public void deleteMusic_shouldReturnEmptyBody() throws Exception {
        super.givenUserAuthenticatedWithPermission(PermissionEnum.SPACE_OWNER);
        givenSpaceInDatabase();
        givenSpaceUserAppAssociationInDatabase(false);
        givenMusicInDatabase();
        whenRequestDeleteDeleteMusic();
        thenShouldReturnEmptyBody();
    }

    @Test
    public void deleteMusic_shouldReturnErrorIfMusicNotExistInSpace() throws Exception {
        super.givenUserAuthenticatedWithPermission(PermissionEnum.SPACE_OWNER);
        givenSpaceInDatabase();
        givenSpaceUserAppAssociationInDatabase(false);
        whenRequestDeleteDeleteMusic();
        thenReturnHttpError400_BadRequest(perform, messagesService.get("music.not.exist.space"));
    }

    @Test
    public void deleteMusic_shouldReturnErrorIfPermissionIsParticipant() throws Exception {
        whenRequestDeleteDeleteMusic();
        thenReturnHttpError403_ForbiddenWithPermission(PermissionEnum.PARTICIPANT, perform, messagesService.get(ERROR_403_DEFAULT));
    }

    private void whenRequestGetFindAllMusic() throws Exception {
        String spaceTestId = getIdOrOtherInvalid(spaceTest);
        String basePath = URL_TEMPLATE.replace(PATH_PARAM_SPACE_ID, spaceTestId);

        super.whenRequestGet(basePath);
    }

    private void whenRequestGetFindById() throws Exception {
        String spaceTestId = getIdOrOtherInvalid(spaceTest);
        String musicTestId = getIdOrOtherInvalid(musicTest);

        String basePath = URL_TEMPLATE.replace(PATH_PARAM_SPACE_ID, spaceTestId);
        String endpoint = basePath.concat("/" + musicTestId);

        super.whenRequestGet(endpoint);
    }

    private void whenRequestGetFindAllSinger() throws Exception {
        String spaceTestId = getIdOrOtherInvalid(spaceTest);

        String basePath = URL_TEMPLATE.replace(PATH_PARAM_SPACE_ID, spaceTestId);
        String endpoint = basePath.concat("/singers");

        super.whenRequestGet(endpoint);
    }

    private void whenRequestPostCreateMusic() throws Exception {
        String spaceTestId = getIdOrOtherInvalid(spaceTest);

        String basePath = URL_TEMPLATE.replace(PATH_PARAM_SPACE_ID, spaceTestId);

        super.whenRequestPost(basePath, musicWithSingerAndLinksDtoTest);
    }

    private void whenRequestPutUpdateMusic() throws Exception {
        String spaceTestId = getIdOrOtherInvalid(spaceTest);
        String musicTestId = getIdOrOtherInvalid(musicTest);
        String singerTestId = getIdOrOtherInvalid(singerTest);

        String basePath = URL_TEMPLATE.replace(PATH_PARAM_SPACE_ID, spaceTestId);
        String endpoint = basePath.concat("/" + musicTestId);

        musicWithSingerAndLinksDtoTest.setId(musicTestId);
        musicWithSingerAndLinksDtoTest.getSinger().setId(singerTestId);
        super.whenRequestPut(endpoint, musicWithSingerAndLinksDtoTest);
    }

    private void whenRequestDeleteDeleteMusic() throws Exception {
        String spaceTestId = getIdOrOtherInvalid(spaceTest);
        String musicTestId = getIdOrOtherInvalid(musicTest);

        String basePath = URL_TEMPLATE.replace(PATH_PARAM_SPACE_ID, spaceTestId);
        String endpoint = basePath.concat("/" + musicTestId);

        super.whenRequestDelete(endpoint);
    }

    protected void thenShouldReturnMusicWithSingerAndLinksDto() throws Exception {
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.id").exists())
                .andExpect(jsonPath("$.content.name").exists())
                .andExpect(jsonPath("$.content.musicStatus").exists())
                .andExpect(jsonPath("$.content.singer").exists())
                .andExpect(jsonPath("$.content.singer.id").exists())
                .andExpect(jsonPath("$.content.singer.name").exists())
                .andExpect(jsonPath("$.content.links").isArray())
        ;
    }

}
