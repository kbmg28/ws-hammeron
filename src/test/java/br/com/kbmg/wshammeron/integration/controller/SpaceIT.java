package br.com.kbmg.wshammeron.integration.controller;

import br.com.kbmg.wshammeron.dto.space.MySpace;
import br.com.kbmg.wshammeron.dto.space.SpaceDto;
import br.com.kbmg.wshammeron.enums.PermissionEnum;
import br.com.kbmg.wshammeron.enums.SpaceStatusEnum;
import br.com.kbmg.wshammeron.integration.BaseEntityIntegrationTests;
import br.com.kbmg.wshammeron.integration.annotations.AllPermissionsParameterizedTest;
import br.com.kbmg.wshammeron.integration.annotations.AllSpaceStatusParameterizedTest;
import builder.SpaceBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.ERROR_403_DEFAULT;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.SPACE_ALREADY_EXIST;
import static br.com.kbmg.wshammeron.integration.ResponseErrorExpect.thenReturnHttpError400_BadRequest;
import static br.com.kbmg.wshammeron.integration.ResponseErrorExpect.thenReturnHttpError403_ForbiddenWithPermission;
import static constants.BaseTestsConstants.BEARER_TOKEN_TEST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SpaceIT extends BaseEntityIntegrationTests {

    private static final String URL_TEMPLATE = "/api/spaces";
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
    void requestNewSpaceForUser_shouldReturnContentEmpty() throws Exception {
        givenSpaceRequestDto();
        whenRequestPostRequestNewSpaceForUser();
        thenShouldReturnEmptyBody();
    }

    @Test
    void requestNewSpaceForUser_shouldReturnErrorIfSpaceAlreadyExist() throws Exception {
        String errorMessage = String.format(messagesService.get(SPACE_ALREADY_EXIST), spaceTest.getName());
        givenSpaceRequestDto(spaceTest.getName());
        whenRequestPostRequestNewSpaceForUser();
        thenReturnHttpError400_BadRequest(perform, errorMessage);
    }

    @Test
    void approveNewSpaceForUser_shouldReturnContentEmptyToSysAdmin() throws Exception {
        givenSysAdmin();
        givenSpaceWithStatus(SpaceStatusEnum.REQUESTED);
        givenSpaceApproveDto();
        whenRequestPostApproveNewSpaceForUser();
        thenShouldReturnEmptyBody();
    }

    @AllPermissionsParameterizedTest
    void approveNewSpaceForUser_shouldReturnError403ToCommonUsers(PermissionEnum permission) throws Exception {
        givenUserAuthenticatedWithPermission(permission);
        givenSpaceApproveDto();
        whenRequestPostApproveNewSpaceForUser();
        thenReturnHttpError403_ForbiddenWithPermission(permission, perform, messagesService.get(ERROR_403_DEFAULT));
    }

    @AllSpaceStatusParameterizedTest
    void findAllSpaceToApprove_shouldReturnSpaceDtoListByStatusToSysAdmin(SpaceStatusEnum spaceStatusEnum) throws Exception {
        givenSysAdmin();
        givenSpaceWithStatus(spaceStatusEnum);
        whenRequestGetFindAllSpaceToApprove();
        thenShouldReturnSpaceDtoList();
    }

    @AllPermissionsParameterizedTest
    void findAllSpaceToApprove_shouldReturnError403ToCommonUsers(PermissionEnum permission) throws Exception {
        givenUserAuthenticatedWithPermission(permission);
        whenRequestGetFindAllSpaceToApprove();
        thenReturnHttpError403_ForbiddenWithPermission(permission, perform, messagesService.get(ERROR_403_DEFAULT));
    }

    @Test
    void findAllSpacesByUserApp_shouldReturnMySpaceList() throws Exception {
        whenRequestGetFindAllSpacesByUserApp();
        thenShouldReturnMySpaceList();
    }

    @Test
    void findAllSpacesByUserApp_shouldReturnMySpaceListToSysAdmin() throws Exception {
        givenSysAdmin();
        whenRequestGetFindAllSpacesByUserApp();
        thenShouldReturnMySpaceList();
    }

    @Test
    void findLastAccessedSpace_shouldReturnMySpace() throws Exception {
        whenRequestGetFindLastAccessedSpace();
        thenShouldReturnMySpace();
    }

    @Test
    void findSpaceOverview_shouldReturnSpaceOverviewDto() throws Exception {
        whenRequestGetFindSpaceOverview();
        thenShouldReturnSpaceOverviewDto();
    }

    @Test
    void changeViewSpaceUser_shouldReturnNewJwtToken() throws Exception {
        whenCallJwtServiceUpdateSpaceOnTokenShouldReturnValidJwt();
        whenRequestPutChangeViewSpaceUser();
        thenShouldReturnJwtUpdated();
    }

    @Test
    void changeViewSpaceUser_shouldReturnNewJwtTokenToSysAdmin() throws Exception {
        givenSysAdmin();
        whenCallJwtServiceUpdateSpaceOnTokenShouldReturnValidJwt();
        whenRequestPutChangeViewSpaceUser();
        thenShouldReturnJwtUpdated();
    }

    private void whenCallJwtServiceUpdateSpaceOnTokenShouldReturnValidJwt() {
        when(jwtServiceMockBean.updateSpaceOnToken(any(), any())).thenReturn(BEARER_TOKEN_TEST);
    }

    private void whenRequestPostRequestNewSpaceForUser() throws Exception {
        String endpoint = URL_TEMPLATE.concat("/request");
        super.whenRequestPost(endpoint, spaceRequestDtoTest);
    }

    private void whenRequestPostApproveNewSpaceForUser() throws Exception {
        String endpoint = String.format("%s/%s/approve", URL_TEMPLATE, spaceTest.getId());
        super.whenRequestPost(endpoint, spaceApproveDtoTest);
    }

    private void whenRequestGetFindAllSpaceToApprove() throws Exception {
        String endpoint = String.format("%s/status/%s", URL_TEMPLATE, spaceTest.getSpaceStatus());
        super.whenRequestGet(endpoint);
    }

    private void whenRequestGetFindAllSpacesByUserApp() throws Exception {
        super.whenRequestGet(URL_TEMPLATE);
    }

    private void whenRequestGetFindLastAccessedSpace() throws Exception {
        super.whenRequestGet(URL_TEMPLATE.concat("/last"));
    }

    private void whenRequestGetFindSpaceOverview() throws Exception {
        super.whenRequestGet(URL_TEMPLATE.concat("/overview"));
    }

    private void whenRequestPutChangeViewSpaceUser() throws Exception {
        String endpoint = String.format("%s/%s/change-view", URL_TEMPLATE, spaceTest.getId());
        super.whenRequestPut(endpoint);
    }

    protected void thenShouldReturnSpaceDtoList() throws Exception {
        super.thenShouldReturnList();

        SpaceDto spaceDtoExpected = SpaceBuilder.generateSpaceDto(spaceTest, userAppLoggedTest);

        thenShouldVerifyIfListContainsTheElementExpected(
                getContent(new TypeReference<>() {}),
                spaceDtoExpected,
                dto -> dto.getSpaceId().equals(spaceTest.getId()));
    }

    protected void thenShouldReturnMySpaceList() throws Exception {
        super.thenShouldReturnList();

        MySpace expected = SpaceBuilder.generateMySpace(spaceTest);

        thenShouldVerifyIfListContainsTheElementExpected(
                getContent(new TypeReference<>() {}),
                expected,
                dto -> dto.getSpaceId().equals(spaceTest.getId()));
    }

    protected void thenShouldReturnMySpace() throws Exception {
        perform.andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.content.spaceId").value(spaceTest.getId()))
                .andExpect(jsonPath("$.content.name").value(spaceTest.getName()))
                .andExpect(jsonPath("$.content.lastAccessed").value(true))
        ;
    }

    protected void thenShouldReturnSpaceOverviewDto() throws Exception {
        perform.andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.content.spaceId").value(spaceTest.getId()))
                .andExpect(jsonPath("$.content.spaceName").value(spaceTest.getName()))
                .andExpect(jsonPath("$.content.userList").isArray())
                .andExpect(jsonPath("$.content.musicList").isArray())
                .andExpect(jsonPath("$.content.eventList").isArray())
        ;
    }

    protected void thenShouldReturnJwtUpdated() throws Exception {
        perform.andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.content").value(BEARER_TOKEN_TEST))
        ;
    }

}
