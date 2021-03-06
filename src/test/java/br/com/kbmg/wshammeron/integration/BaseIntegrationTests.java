package br.com.kbmg.wshammeron.integration;

import br.com.kbmg.wshammeron.config.AppConfig;
import br.com.kbmg.wshammeron.config.messages.MessagesService;
import br.com.kbmg.wshammeron.config.recaptcha.v3.AbstractCaptchaService;
import br.com.kbmg.wshammeron.config.recaptcha.v3.RecaptchaEnum;
import br.com.kbmg.wshammeron.dto.space.SpaceApproveDto;
import br.com.kbmg.wshammeron.dto.space.SpaceRequestDto;
import br.com.kbmg.wshammeron.enums.PermissionEnum;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.SpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.model.UserPermission;
import br.com.kbmg.wshammeron.repository.SpaceRepository;
import br.com.kbmg.wshammeron.repository.SpaceUserAppAssociationRepository;
import br.com.kbmg.wshammeron.repository.UserAppRepository;
import br.com.kbmg.wshammeron.repository.UserPermissionRepository;
import br.com.kbmg.wshammeron.service.JwtService;
import br.com.kbmg.wshammeron.service.SmsService;
import br.com.kbmg.wshammeron.util.response.ResponseData;
import builder.SpaceBuilder;
import builder.UserBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static br.com.kbmg.wshammeron.constants.JwtConstants.CLAIM_SPACE_ID;
import static br.com.kbmg.wshammeron.constants.JwtConstants.CLAIM_SPACE_NAME;
import static constants.BaseTestsConstants.ANY_VALUE;
import static constants.BaseTestsConstants.BEARER_TOKEN_TEST;
import static constants.BaseTestsConstants.TOKEN_TEST;
import static constants.BaseTestsConstants.generateRandomEmail;
import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.CollectionUtils.isEmpty;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AppConfig.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
            "mail=mail@test.com",
            "mailPassSocial=senha123",
            "mail=mail@test.com",
            "show-sql=true",
            "recaptchaKeySite=key_site_integration_test",
            "recaptchaKeySecret=key_secret_integration_test",
            "recaptchaThreshold=0.8",
            "app.logs=true",
            "profile=h2"
        }
)
@AutoConfigureMockMvc
@Transactional
@Tag("integrationTest")
public abstract class BaseIntegrationTests {

    protected static String testJsonRequest;
    protected static HttpHeaders headers = new HttpHeaders();
//    protected static ObjectMapper objectMapper = new ObjectMapper();
    protected static Gson gson = new Gson();
    protected UserApp userAppLoggedTest;
    protected SpaceUserAppAssociation spaceUserAppAssociationOfUserLoggedTest;
    protected Space spaceTest;
    protected SpaceRequestDto spaceRequestDtoTest;
    protected SpaceApproveDto spaceApproveDtoTest;

    protected static ResultActions perform;
    protected static ResultActions resultActions;

    protected static final String templateUrlRecaptcha="%s?g-recaptcha-response=" + ANY_VALUE;

    protected LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();

    /* Beans -> No invoke real method */
    @MockBean
    protected JavaMailSender mailSenderMockBean;

    @MockBean
    protected AbstractCaptchaService recaptchaServiceMockBean;

    @MockBean
    protected JwtService jwtServiceMockBean;

    @MockBean
    protected SmsService smsServiceMockBean;

    /* Instances */
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    public MessagesService messagesService;

    @Autowired
    protected UserAppRepository userAppRepository;

    @Autowired
    protected SpaceRepository spaceRepository;

    @Autowired
    protected UserPermissionRepository userPermissionRepository;

    @Autowired
    protected SpaceUserAppAssociationRepository spaceUserAppAssociationRepository;

    protected void givenHeadersRequired() {
        headers.add(Constants.AUTHORIZATION_HEADER_NAME, BEARER_TOKEN_TEST);
    }

    protected void givenUserAuthenticatedWithPermission(PermissionEnum permissionEnum) {
        associatePermissionToUserLogged(permissionEnum);
    }

    protected void givenUserAuthenticatedWithoutRoles() {
        this.saveUserAuthenticated(null);
    }

    protected void givenSysAdmin() {
        userAppLoggedTest.setIsSysAdmin(true);
        userAppRepository.save(userAppLoggedTest);
    }

    protected void checkIfEmailAlreadyExistAndDeleteIfPresent(){
        userAppRepository.findByEmailIgnoreCase(generateRandomEmail())
                .ifPresent(this::deleteUserAndAssociations);
    }

    protected void deleteUserAndAssociations(UserApp userInDatabase) {
        if (userInDatabase != null) {

            if(!isEmpty(userInDatabase.getSpaceUserAppAssociationList())) {
                spaceUserAppAssociationRepository.deleteAll(userInDatabase.getSpaceUserAppAssociationList());
            }

            userAppRepository.delete(userInDatabase);
            userInDatabase = null;
        }
    }

    protected void givenUserOnDatabase(UserApp userApp) {
        userAppRepository.save(userApp);
    }

    protected void givenSpaceOnDatabase(Space space) {
        spaceRepository.save(space);
    }

    private void associatePermissionToUserLogged(PermissionEnum permissionEnum) {
        if (userAppLoggedTest == null) {
            this.saveUserAuthenticated(permissionEnum);
        } else {
            addPermissionToUserAppLogged(permissionEnum);
        }
    }

    private void saveUserAuthenticated(PermissionEnum permission) {
        if(isEmpty(headers)){
            givenHeadersRequired();
        }

        if (userAppLoggedTest == null) {
            userAppLoggedTest = UserBuilder.generateUserAppLogged();
            givenUserOnDatabase(userAppLoggedTest);
        }

        if (spaceTest == null) {
            spaceTest = SpaceBuilder.generateSpace(userAppLoggedTest);
            givenSpaceOnDatabase(spaceTest);
        }

        addPermissionToUserAppLogged(permission);
    }

    private void addPermissionToUserAppLogged(PermissionEnum permission) {
        if (permission != null) {
            givenSpaceUserAppAssociationOnDatabase();

            Set<UserPermission> userPermissionList = spaceUserAppAssociationOfUserLoggedTest.getUserPermissionList();

            if (isNotEmpty(userPermissionList)) {
                userPermissionList
                        .stream()
                        .findFirst()
                        .ifPresent(userPermission -> {
                            userPermission.setPermission(permission);
                            userPermissionRepository.save(userPermission);
                        });
            } else {
                UserPermission userPermission = UserBuilder.generateUserPermission(
                        spaceUserAppAssociationOfUserLoggedTest, permission);
                userPermission.setId(null);

                userPermissionRepository.save(userPermission);
            }
        }
    }

    private void givenSpaceUserAppAssociationOnDatabase() {
        if (spaceUserAppAssociationOfUserLoggedTest == null) {
            spaceUserAppAssociationOfUserLoggedTest = UserBuilder.generateSpaceUserAppAssociation(userAppLoggedTest, spaceTest);
            spaceUserAppAssociationOfUserLoggedTest.setId(null);
            spaceUserAppAssociationRepository.save(spaceUserAppAssociationOfUserLoggedTest);

            spaceTest.getSpaceUserAppAssociationList().add(spaceUserAppAssociationOfUserLoggedTest);
        }
    }

    protected void whenRequestGet(String urlTemplate)
        throws Exception {

        checkUrlToApi(urlTemplate);
        perform = mockMvc.perform(MockMvcRequestBuilders.get(urlTemplate)
                .headers(headers)
                .params(requestParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    private void checkUrlToApi(String urlTemplate) {
        if (urlTemplate.contains("/api/")) {

            when(jwtServiceMockBean.validateTokenAndGetUserId(TOKEN_TEST)).thenReturn(userAppLoggedTest.getId());
            when(jwtServiceMockBean.getValue(TOKEN_TEST, CLAIM_SPACE_ID)).thenReturn(spaceTest.getId());
            when(jwtServiceMockBean.getValue(TOKEN_TEST, CLAIM_SPACE_NAME)).thenReturn(spaceTest.getName());
        }
    }

    protected <T> void whenRequestPost(String urlTemplate, T body)
        throws Exception {
        checkUrlToApi(urlTemplate);
        testJsonRequest = gson.toJson(body);

        perform = mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                .headers(headers)
                .params(requestParams)
                .content(testJsonRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    protected void whenRequestDelete(String urlTemplate)
        throws Exception {
        checkUrlToApi(urlTemplate);

        perform = mockMvc.perform(MockMvcRequestBuilders.delete(urlTemplate)
                .headers(headers)
                .params(requestParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    protected void whenRequestPut(String urlTemplate)
        throws Exception {
        checkUrlToApi(urlTemplate);

        perform = mockMvc.perform(MockMvcRequestBuilders.put(urlTemplate)
                .headers(headers)
                .params(requestParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    protected <T> void whenRequestPut(String urlTemplate, T body)
        throws Exception {
        checkUrlToApi(urlTemplate);
        testJsonRequest = gson.toJson(body);

        perform = mockMvc.perform(MockMvcRequestBuilders.put(urlTemplate)
                .headers(headers)
                .params(requestParams)
                .content(testJsonRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
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

    protected <T> T getContent(TypeReference<ResponseData<T>> typeReference) throws JsonProcessingException, UnsupportedEncodingException {
        String contentAsString = perform.andReturn().getResponse().getContentAsString();
        ResponseData<T> listResponseData = objectMapper.readValue(contentAsString, typeReference);

        return listResponseData.getContent();
    }

    protected <T> T getOneElementOfList(Collection<T> spaceDtoList, Predicate<T> predicate) {
        return spaceDtoList
                .stream()
                .filter(predicate)
                .findFirst()
                .orElseThrow();
    }

    protected <T> void thenShouldVerifyIfListContainsTheElementExpected(List<T> listResult,
                                                                        T spaceDtoExpected,
                                                                        Predicate<T> predicate) {
        T spaceDtoResult = getOneElementOfList(listResult, predicate);
        assertEquals(spaceDtoExpected, spaceDtoResult);
    }

    protected void thenCheckIfRecaptchaServiceInvoked(RecaptchaEnum module) {
        verify(recaptchaServiceMockBean, times(1)).processResponse(ANY_VALUE, module.getValue());
    }

    protected void thenShouldReturnList() throws Exception {
        perform.andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isNotEmpty());
    }

    protected void thenShouldReturnEmptyBody() throws Exception {
        perform.andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.content").doesNotExist())
                .andExpect(jsonPath("$").isEmpty());
    }

}
