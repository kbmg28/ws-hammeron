package br.com.kbmg.wshammeron.integration;

import br.com.kbmg.wshammeron.enums.PermissionEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class ResponseErrorExpect {

    public static void thenReturnHttpError400_BadRequest(ResultActions perform, String msgErrorExpected) throws Exception {
        perform.andExpect(status().isBadRequest());
        expectMsgInBody(perform, "400 - Bad Request", msgErrorExpected);
    }

    public static void thenReturnHttpError401_Unauthorized(ResultActions perform, String msgErrorExpected) throws Exception {
        perform.andExpect(status().isUnauthorized());
        expectMsgInBody(perform, "401 - Unauthorized", msgErrorExpected);
    }

    public static void thenReturnHttpError403_Forbidden(ResultActions perform, String msgErrorExpected) throws Exception {
        perform.andExpect(status().isForbidden());
        expectMsgInBody(perform, "403 - Forbidden", msgErrorExpected);
    }

    public static void thenReturnHttpError403_ForbiddenWithPermission(PermissionEnum permissionEnum, ResultActions perform, String msgErrorExpected) throws Exception {
        perform.andExpect(result -> assertEquals(String.format("Permission [%s]", permissionEnum.toString()), HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus()));
        expectMsgInBody(perform, "403 - Forbidden", msgErrorExpected);
    }

    private static void expectMsgInBody(ResultActions perform, String httpStatus, String msgErrorExpected) throws Exception {
        perform.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error.httpStatus").value(httpStatus))
                .andExpect(jsonPath("$.error.message").value(msgErrorExpected))
        ;
    }
}
