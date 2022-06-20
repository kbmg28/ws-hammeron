package br.com.kbmg.wshammeron.integration.controller;

import br.com.kbmg.wshammeron.enums.PermissionEnum;
import br.com.kbmg.wshammeron.integration.BaseEntityIntegrationTests;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.ERROR_403_DEFAULT;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.MUSIC_ALREADY_EXIST_SPACE;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.MUSIC_NOT_EXIST_SPACE;
import static br.com.kbmg.wshammeron.integration.ResponseErrorExpect.thenReturnHttpError400_BadRequest;
import static br.com.kbmg.wshammeron.integration.ResponseErrorExpect.thenReturnHttpError403_ForbiddenWithPermission;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MusicIT extends BaseEntityIntegrationTests {

    private static final String URL_TEMPLATE = "/api/musics";
    private static final String PATH_PARAM_SPACE_ID = "{space-id}";

    @BeforeEach
    public void beforeEach() {
        givenUserAuthenticatedWithPermission(PermissionEnum.PARTICIPANT);
    }

   @AfterEach
    public void afterEach() {
        super.deleteUserAndAssociations(userAppLoggedTest);
    }

    @Test
    void findAllMusic_shouldReturnMusicWithSingerAndLinksDtoList() throws Exception {
        givenMusicInDatabase();
        whenRequestGetFindAllMusic();
        thenShouldReturnList();
    }

    @Test
    void findById_shouldReturnMusicWithSingerAndLinksDto() throws Exception {
        givenMusicInDatabase();
        whenRequestGetFindByIdNoEvents();
        thenShouldReturnMusicWithSingerAndLinksDto();
    }

    @Test
    void findById_shouldReturnErrorIfMusicNotExistInSpace() throws Exception {
        whenRequestGetFindByIdNoEvents();
        thenReturnHttpError400_BadRequest(perform, messagesService.get(MUSIC_NOT_EXIST_SPACE));
    }

    @Test
    void findAllSinger_shouldReturnSingerDtoList() throws Exception {
        givenMusicInDatabase();
        whenRequestGetFindAllSinger();
        thenShouldReturnList();
    }

    @Test
    void createMusic_shouldReturnMusicWithSingerAndLinksDto() throws Exception {
        givenMusicWithSingerAndLinksDto();
        whenRequestPostCreateMusic();
        thenShouldReturnMusicWithSingerAndLinksDto();
    }

    @Test
    void createMusic_shouldReturnErrorIfMusicAlreadyExistInSpace() throws Exception {
        givenMusicInDatabase();
        givenMusicWithSingerAndLinksDto();
        whenRequestPostCreateMusic();
        thenReturnHttpError400_BadRequest(perform, messagesService.get(MUSIC_ALREADY_EXIST_SPACE));
    }

    @Test
    void updateMusic_shouldReturnMusicWithSingerAndLinksDto() throws Exception {
        givenMusicWithSingerAndLinksDto();
        givenMusicInDatabase();
        whenRequestPutUpdateMusic();
        thenShouldReturnMusicWithSingerAndLinksDto();
    }

    @Test
    void updateMusic_shouldReturnErrorIfMusicNotExistInSpace() throws Exception {
        givenMusicWithSingerAndLinksDto();
        whenRequestPutUpdateMusic();
        thenReturnHttpError400_BadRequest(perform, messagesService.get(MUSIC_NOT_EXIST_SPACE));
    }

    @Test
    void deleteMusic_shouldReturnEmptyBody() throws Exception {
        super.givenUserAuthenticatedWithPermission(PermissionEnum.SPACE_OWNER);
        givenMusicInDatabase();
        whenRequestDeleteDeleteMusic();
        thenShouldReturnEmptyBody();
    }

    @Test
    void deleteMusic_shouldReturnErrorIfMusicNotExistInSpace() throws Exception {
        super.givenUserAuthenticatedWithPermission(PermissionEnum.SPACE_OWNER);
        whenRequestDeleteDeleteMusic();
        thenReturnHttpError400_BadRequest(perform, messagesService.get(MUSIC_NOT_EXIST_SPACE));
    }

    @Test
    void deleteMusic_shouldReturnErrorIfPermissionIsParticipant() throws Exception {
        whenRequestDeleteDeleteMusic();
        thenReturnHttpError403_ForbiddenWithPermission(PermissionEnum.PARTICIPANT, perform, messagesService.get(ERROR_403_DEFAULT));
    }

    private void whenRequestGetFindAllMusic() throws Exception {
        super.whenRequestGet(URL_TEMPLATE);
    }

    private void whenRequestGetFindByIdNoEvents() throws Exception {
        whenRequestGetFindById(false);
    }

    private void whenRequestGetFindByIdWithEvents() throws Exception {
        whenRequestGetFindById(true);
    }

    private void whenRequestGetFindById(boolean hasEvents) throws Exception {
        String musicTestId = getIdOrOtherInvalid(musicTest);

        String endpoint = String.format("%s/%s?eventsFromTheLast3Months=%b", URL_TEMPLATE, musicTestId, hasEvents);

        super.whenRequestGet(endpoint);
    }

    private void whenRequestGetFindAllSinger() throws Exception {
        String endpoint = URL_TEMPLATE.concat("/singers");

        super.whenRequestGet(endpoint);
    }

    private void whenRequestPostCreateMusic() throws Exception {
        super.whenRequestPost(URL_TEMPLATE, musicWithSingerAndLinksDtoTest);
    }

    private void whenRequestPutUpdateMusic() throws Exception {
        String musicTestId = getIdOrOtherInvalid(musicTest);
        String singerTestId = getIdOrOtherInvalid(singerTest);

        String endpoint = URL_TEMPLATE.concat("/" + musicTestId);

        musicWithSingerAndLinksDtoTest.setId(musicTestId);
        musicWithSingerAndLinksDtoTest.getSinger().setId(singerTestId);
        super.whenRequestPut(endpoint, musicWithSingerAndLinksDtoTest);
    }

    private void whenRequestDeleteDeleteMusic() throws Exception {
        String musicTestId = getIdOrOtherInvalid(musicTest);

        String endpoint = URL_TEMPLATE.concat("/" + musicTestId);

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
