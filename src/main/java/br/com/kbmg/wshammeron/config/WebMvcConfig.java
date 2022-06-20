package br.com.kbmg.wshammeron.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.concurrent.TimeUnit;

import static br.com.kbmg.wshammeron.constants.AppConstants.LANGUAGE;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName(LANGUAGE);
        return localeChangeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        this.resourceHandlersForCache(registry);
        this.resourceForHandlersSwaggerUI(registry);
    }

    private void resourceHandlersForCache(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/")
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePrivate().mustRevalidate());

        registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/static/assets/")
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePrivate().mustRevalidate());

        registry.addResourceHandler("/index.html").addResourceLocations("classpath:/static/index.html").setCachePeriod(0);
    }

    private void resourceForHandlersSwaggerUI(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui**").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}
