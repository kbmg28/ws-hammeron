package br.com.kbmg.wsmusiccontrol.integration.controller;

import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.integration.BaseEntityIntegrationTests;
import br.com.kbmg.wsmusiccontrol.integration.annotations.PermissionWithoutSysAdminParameterizedTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static br.com.kbmg.wsmusiccontrol.integration.ResponseErrorExpect.thenReturnHttpError400_BadRequest;
import static br.com.kbmg.wsmusiccontrol.integration.ResponseErrorExpect.thenReturnHttpError403_ForbiddenWithPermission;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserAppIT extends BaseEntityIntegrationTests {

    private static final String URL_TEMPLATE = "/api/spaces/{space-id}/users";
    private static final String PATH_PARAM_SPACE_ID = "{space-id}";

    @BeforeEach
    public void before() {
        super.beforeAllTestsBase();
    }

    @AfterEach
    public void afterEach() {
        super.deleteUserAndAssociations(userAppLoggedTest);
    }

    @PermissionWithoutSysAdminParameterizedTest
    public void findAllBySpace_shouldReturnUserWithPermissionDtoList(PermissionEnum permissionEnum) throws Exception {
        givenUserAuthenticatedWithPermission(permissionEnum);
        givenSpaceInDatabase();
        givenSpaceUserAppAssociationInDatabase(false);
        whenRequestGetFindAllBySpace();
        thenShouldReturnUserWithPermissionDtoList();
    }

    @PermissionWithoutSysAdminParameterizedTest
    public void findAllBySpace_shouldReturnErrorIfUserDoesNotBelongToSpace(PermissionEnum permissionEnum) throws Exception {
        givenUserAuthenticatedWithPermission(permissionEnum);
        givenSpaceInDatabase();
        whenRequestGetFindAllBySpace();
        thenReturnHttpError403_ForbiddenWithPermission(PermissionEnum.PARTICIPANT, perform, messagesService.get("space.user.not.access"));
    }

    @Test
    public void findAllBySpace_shouldReturnToSysAdminUserWithPermissionDtoList() throws Exception {
//        givenUserAuthenticatedWithPermission(PermissionEnum.SYS_ADMIN);
        givenSpaceInDatabase();
        whenRequestGetFindAllBySpace();
        thenShouldReturnUserWithPermissionDtoList();
    }

    @Test
    public void findAllBySpace_shouldReturnToSysAdminErrorIfSpaceNotExist() throws Exception {
//        givenUserAuthenticatedWithPermission(PermissionEnum.SYS_ADMIN);
        whenRequestGetFindAllBySpace();
        thenReturnHttpError400_BadRequest(perform, messagesService.get("space.not.exist"));
    }

    private void whenRequestGetFindAllBySpace() throws Exception {
        String spaceTestId = spaceTest == null ? UUID.randomUUID().toString() : spaceTest.getId();
        String basePath = URL_TEMPLATE.replace(PATH_PARAM_SPACE_ID, spaceTestId);
        String endpoint = basePath.concat("/all");
        super.whenRequestGet(endpoint);
    }

    private void thenShouldReturnUserWithPermissionDtoList() throws Exception {
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
        ;
    }


}
