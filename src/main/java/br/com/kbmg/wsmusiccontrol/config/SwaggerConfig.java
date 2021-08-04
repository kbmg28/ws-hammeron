package br.com.kbmg.wsmusiccontrol.config;

import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;
import java.util.List;

import static br.com.kbmg.wsmusiccontrol.constants.AppConstants.API_DESCRIBE;
import static br.com.kbmg.wsmusiccontrol.constants.AppConstants.LANGUAGE;

@Configuration
@EnableOpenApi
public class SwaggerConfig {

    @Autowired
    private BuildProperties buildProperties;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30).select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build()
                .securityContexts(List.of(jwtSecurityContext(), languageSecurityContext()))
                .securitySchemes(List.of(apiKeyAuthorization(), apiKeyLanguage()))
                .apiInfo(this.apiInfo());
    }

    private ApiInfo apiInfo() {

        return new ApiInfoBuilder()
                .title (API_DESCRIBE)
                .license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                .version(buildProperties.getVersion())
                .contact(new Contact("Kevin Gomes","https://www.linkedin.com/in/kevin-gomes-2b5b58175/", "kb.developer.br@gmail.com"))
                .build();
    }

    private SecurityContext jwtSecurityContext() {
        SecurityReference referenceAuthorization = new SecurityReference(HttpHeaders.AUTHORIZATION, new AuthorizationScope[0]);

        return SecurityContext.builder()
                .securityReferences(Collections.singletonList(referenceAuthorization))
                .operationSelector(o -> o.requestMappingPattern().startsWith("/api"))
                .build();
    }

    private SecurityContext languageSecurityContext() {
        SecurityReference referenceLanguage = new SecurityReference(LANGUAGE, new AuthorizationScope[0]);

        return SecurityContext.builder()
                .securityReferences(Collections.singletonList(referenceLanguage))
                .build();
    }

    public ApiKey apiKeyAuthorization() {
        return new ApiKey(HttpHeaders.AUTHORIZATION, HttpHeaders.AUTHORIZATION, In.HEADER.name());
    }

    public ApiKey apiKeyLanguage() {
        return new ApiKey(LANGUAGE, LANGUAGE, In.QUERY.name());
    }

}