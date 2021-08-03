package integration;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class ResponseErrorExpect {

    public static void thenReturnHttpError400_BadRequest(ResultActions perform, String msgErrorExpected) throws Exception {
        thenReturnHttpError(status().isBadRequest(), perform, msgErrorExpected);
    }

    public static void thenReturnHttpError403_Forbidden(ResultActions perform, String msgErrorExpected) throws Exception {
        thenReturnHttpError(status().isForbidden(), perform, msgErrorExpected);
    }

    public static void thenReturnHttpError403_ForbiddenWithPermission(String role, ResultActions perform, String msgErrorExpected) throws Exception {
        perform.andExpect(result -> assertEquals(String.format("Permission [%s]", role), HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus()));
        expectMsgInBody(perform, msgErrorExpected);
    }

    private static void thenReturnHttpError(org.springframework.test.web.servlet.ResultMatcher httpStatusResultMatcher, ResultActions perform, String msgErrorExpected) throws Exception {
        perform.andExpect(status().isForbidden());
        expectMsgInBody(perform, msgErrorExpected);
    }

    private static void expectMsgInBody(ResultActions perform, String msgErrorExpected) throws Exception {
        perform.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.error.message").value(msgErrorExpected));
    }
}
