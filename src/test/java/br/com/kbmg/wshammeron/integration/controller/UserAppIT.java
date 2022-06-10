package br.com.kbmg.wshammeron.integration.controller;

import br.com.kbmg.wshammeron.dto.user.UserDto;
import br.com.kbmg.wshammeron.enums.PermissionEnum;
import br.com.kbmg.wshammeron.integration.BaseEntityIntegrationTests;
import br.com.kbmg.wshammeron.integration.annotations.AllPermissionsParameterizedTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.USER_DOES_NOT_PERMISSION_TO_ACTION;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.USER_HAS_PERMISSION;
import static br.com.kbmg.wshammeron.integration.ResponseErrorExpect.thenReturnHttpError400_BadRequest;
import static br.com.kbmg.wshammeron.integration.ResponseErrorExpect.thenReturnHttpError403_Forbidden;
import static constants.BaseTestsConstants.generateRandomEmail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserAppIT extends BaseEntityIntegrationTests {

    private static final String URL_TEMPLATE = "/api/users";

    @BeforeEach
    public void before() {
        givenUserAuthenticatedWithPermission(PermissionEnum.PARTICIPANT);
    }

    @AfterEach
    public void afterEach() {
        super.deleteUserAndAssociations(userAppLoggedTest);
    }

    @AllPermissionsParameterizedTest
    void findAllBySpace_shouldReturnUserWithPermissionDtoList(PermissionEnum permissionEnum) throws Exception {
        givenUserAuthenticatedWithPermission(permissionEnum);
        whenRequestGetFindAllBySpace();
        thenShouldReturnUserWithPermissionDtoList();
    }

    @Test
    void findAllBySpace_shouldReturnToSysAdminUserWithPermissionDtoList() throws Exception {
        givenSuperUser();
        whenRequestGetFindAllBySpace();
        thenShouldReturnUserWithPermissionDtoList();
    }

    @Test
    void updateUserLogged_shouldReturnUserLoggedUpdated() throws Exception {
        UserDto userDto = givenUserDto(userAppLoggedTest.getEmail());
        whenRequestPutUserLogged(userDto);
        thenShouldReturnUserLoggedUpdated();
    }

    @Test
    void findUserLogged_shouldReturnUserLogged() throws Exception {
        whenRequestGetUserLogged();
        thenShouldReturnUserLoggedUpdated();
    }

    @Test
    void findUsersAssociationForEventsBySpace_shouldReturnUserList() throws Exception {
        whenRequestGetFindUsersAssociationForEventsBySpace();
        thenShouldReturnUserOnlyIdNameAndEmailDtoList();
    }

    @Test
    void updatePermission_shouldReturnUserList() throws Exception {
        givenUserAuthenticatedWithPermission(PermissionEnum.SPACE_OWNER);
        String otherEmail = generateRandomEmail();
        whenRequestPutUpdatePermission(otherEmail);
        thenShouldReturnContentEmpty();
    }

    @Test
    void updatePermission_shouldReturnErrorIfUserLoggedNoSpaceOwner() throws Exception {
        String otherEmail = generateRandomEmail();
        whenRequestPutUpdatePermission(otherEmail);
        thenReturnHttpError403_Forbidden(perform, messagesService.get(USER_DOES_NOT_PERMISSION_TO_ACTION));
    }

    @Test
    void updatePermission_shouldReturnErrorIfUserHasPermissions() throws Exception {
        whenRequestPutUpdatePermission(userAppLoggedTest.getEmail());
        thenReturnHttpError400_BadRequest(perform, messagesService.get(USER_HAS_PERMISSION));
    }

    @Test
    void deleteUserByEmailCascade_shouldReturnContentEmpty() throws Exception {
        givenSuperUser();
        whenRequestDeleteUserByEmailCascade("");
        thenShouldReturnContentEmpty();
    }

    private void whenRequestGetFindAllBySpace() throws Exception {
        super.whenRequestGet(URL_TEMPLATE);
    }

    private void whenRequestPutUserLogged(UserDto userDto) throws Exception {
        String endpoint = URL_TEMPLATE.concat("/logged");
        super.whenRequestPut(endpoint, userDto);
    }

    private void whenRequestGetUserLogged() throws Exception {
        String endpoint = URL_TEMPLATE.concat("/logged");
        super.whenRequestGet(endpoint);
    }

    private void whenRequestGetFindUsersAssociationForEventsBySpace() throws Exception {
        String endpoint = URL_TEMPLATE.concat("/association-for-events");
        super.whenRequestGet(endpoint);
    }

    private void whenRequestPutUpdatePermission(String email) throws Exception {
        String endpoint = String.format("%s/%s/permissions/%s", URL_TEMPLATE, email, PermissionEnum.PARTICIPANT);
        super.whenRequestPut(endpoint);
    }

    private void whenRequestDeleteUserByEmailCascade(String email) throws Exception {
        String endpoint = String.format("%s/%s", URL_TEMPLATE, userAppLoggedTest.getEmail());
        super.whenRequestDelete(endpoint);
    }

    private void thenShouldReturnUserWithPermissionDtoList() throws Exception {
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
        ;
    }
    private void thenShouldReturnUserLoggedUpdated() throws Exception {
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.id").value(userAppLoggedTest.getId()))
                .andExpect(jsonPath("$.content.name").value(userAppLoggedTest.getName()))
                .andExpect(jsonPath("$.content.email").value(userAppLoggedTest.getEmail()))
                .andExpect(jsonPath("$.content.cellPhone").value(userAppLoggedTest.getCellPhone()))
        ;
    }
    private void thenShouldReturnUserOnlyIdNameAndEmailDtoList() throws Exception {
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*].userId").value(userAppLoggedTest.getId()))
                .andExpect(jsonPath("$.content.[*].name").value(userAppLoggedTest.getName()))
                .andExpect(jsonPath("$.content.[*].email").value(userAppLoggedTest.getEmail()))
        ;
    }


}
