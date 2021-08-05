package br.com.kbmg.wsmusiccontrol.integration;

import br.com.kbmg.wsmusiccontrol.config.AppConfig;
import br.com.kbmg.wsmusiccontrol.config.messages.MessagesService;
import br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants;
import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.UserPermission;
import br.com.kbmg.wsmusiccontrol.repository.UserAppRepository;
import br.com.kbmg.wsmusiccontrol.repository.UserPermissionRepository;
import builder.UserBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.tomcat.websocket.Constants;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Set;

import static constants.BaseTestsConstants.TOKEN;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AppConfig.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = { "mail=mail@test.com", "mailPassSocial=senha123" })
@AutoConfigureMockMvc
@Transactional
@Tag("integrationTest")
public abstract class BaseIntegrationTests {

    protected static String testJsonRequest;
    protected static HttpHeaders headers;
    protected static ObjectMapper objectMapper = new ObjectMapper();
    protected static Gson gson = new Gson();
    protected static UserApp userAppLoggedTest;
    protected static ResultActions perform;
    protected static ResultActions resultActions;

    protected LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    public MessagesService messagesService;

    @Autowired
    protected UserAppRepository userAppRepository;

    @Autowired
    protected UserPermissionRepository userPermissionRepository;

    @MockBean
    protected JavaMailSender mailSender;

    protected void beforeAllTestsBase() {
        givenHeadersRequired();
        givenUserAuthenticatedWithoutRoles();
    }

    protected void givenHeadersRequired() {
        headers = new HttpHeaders();
        headers.add(Constants.AUTHORIZATION_HEADER_NAME, TOKEN);
    }

    protected void givenUserAuthenticatedAdmin() {
        this.saveUserAuthenticated(PermissionEnum.ADMIN);
    }

    protected void givenUserAuthenticatedWithoutRoles() {
        this.saveUserAuthenticated();
    }

    private void saveUserAuthenticated(PermissionEnum... permission) {
        givenHeadersRequired();

        userAppLoggedTest = UserBuilder.generateUserAppLogged();
        userAppRepository.save(userAppLoggedTest);

        Set<UserPermission> userPermissions = UserBuilder.generateUserPermissions(userAppLoggedTest, permission);
        userPermissionRepository.saveAll(userPermissions);
    }

    protected void whenRequestGet(String urlTemplate)
        throws Exception {

        perform = mockMvc.perform(MockMvcRequestBuilders.get(urlTemplate)
                .headers(headers)
                .params(requestParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    protected <T> void whenRequestPost(String urlTemplate, T body)
        throws Exception {
        testJsonRequest = gson.toJson(body);

        perform = mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                .headers(headers)
                .params(requestParams)
                .content(testJsonRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    protected void whenRequestDelete(String urlTemplate)
        throws Exception {

        perform = mockMvc.perform(MockMvcRequestBuilders.delete(urlTemplate)
                .headers(headers)
                .params(requestParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    protected void whenRequestPut(String urlTemplate)
        throws Exception {

        perform = mockMvc.perform(MockMvcRequestBuilders.put(urlTemplate)
                .headers(headers)
                .params(requestParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    protected <T> void whenRequest_thenShouldReturnWithHttpError403_Forbidden(RequestMethod requestMethod, String urlTemplate, Object... args)
        throws Exception {
        switchMethodToWhenRequest(requestMethod, urlTemplate, args);
        ResponseErrorExpect.thenReturnHttpError403_Forbidden(perform, messagesService.get(KeyMessageConstants.ERROR_403_DEFAULT));
    }

    protected <T> void whenRequest_thenShouldReturnWithHttpError403_Forbidden(PermissionEnum role, RequestMethod requestMethod, String urlTemplate,
                                                                              Object... args) throws Exception {
        switchMethodToWhenRequest(requestMethod, urlTemplate, args);
        ResponseErrorExpect.thenReturnHttpError403_ForbiddenWithPermission(role.name(), perform, messagesService.get(KeyMessageConstants.ERROR_403_DEFAULT));
    }

    private void switchMethodToWhenRequest(RequestMethod requestMethod, String urlTemplate, Object[] args) throws Exception {
        if (testJsonRequest == null) {
            perform = mockMvc.perform(switchMethod(requestMethod, urlTemplate, args).headers(headers).contentType(MediaType.APPLICATION_JSON_VALUE));
        } else {
            perform = mockMvc.perform(
                    switchMethod(requestMethod, urlTemplate, args).headers(headers).contentType(MediaType.APPLICATION_JSON_VALUE).content(testJsonRequest));
        }
    }

    private MockHttpServletRequestBuilder switchMethod(RequestMethod requestMethod, String urlTemplate, Object... args) {
        MockHttpServletRequestBuilder res;

        switch (requestMethod) {
            case POST:
                res = MockMvcRequestBuilders.post(urlTemplate, args);
                break;
            case PUT:
                res = MockMvcRequestBuilders.put(urlTemplate, args);
                break;
            case DELETE:
                res = MockMvcRequestBuilders.delete(urlTemplate, args);
                break;
            case GET:
                res = MockMvcRequestBuilders.get(urlTemplate, args);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestMethod);
        }

        return res;
    }

    protected <T> T parseContentForObject(Type type) throws UnsupportedEncodingException {
        String content = performSplitFromContentAsString();
        return gson.fromJson(content, type);
    }

    protected <T> T parseContentForObject(Class<T> classDestination) throws UnsupportedEncodingException {
        String content = performSplitFromContentAsString();
        return gson.fromJson(content, classDestination);
    }

    private String performSplitFromContentAsString() throws UnsupportedEncodingException {
        String contentAsString = perform.andReturn().getResponse().getContentAsString();
        int index = contentAsString.indexOf("\"content\":") + 10;
        String substring = contentAsString.substring(index, contentAsString.length() - 1);
        String str = contentAsString.split("\"content\":")[1];
        return (str == null) ? "" : str.substring(0, str.length() - 1);
    }
}
