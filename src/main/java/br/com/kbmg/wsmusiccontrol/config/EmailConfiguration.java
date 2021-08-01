package br.com.kbmg.wsmusiccontrol.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;

//@ComponentScan(basePackages = { "br.com.kbmg.wsmusiccontrol" })
//@PropertySource(value={"classpath:application.properties"})
@Component
public class EmailConfiguration {

    @Value("${spring.mail.host}")
    private String mailServerHost;

    @Value("${spring.mail.port}")
    private Integer mailServerPort;

    @Value("${spring.mail.username}")
    private String mailServerUsername;

    @Value("${spring.mail.password}")
    private String mailServerPassword;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String mailServerAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String mailServerStartTls;

//    @Value("${spring.mail.templates.path}")
//    private String mailTemplatesPath;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(mailServerHost);
        mailSender.setPort(mailServerPort);

        mailSender.setUsername(mailServerUsername);
        mailSender.setPassword(mailServerPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", mailServerAuth);
        props.put("mail.smtp.starttls.enable", mailServerStartTls);
        props.put("mail.debug", "true");

        return mailSender;
    }
//@Bean
//public JavaMailSender getJavaMailSender() {
//    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//    mailSender.setHost("smtp.gmail.com");
//    mailSender.setPort(587);
//
//    mailSender.setUsername("kb.developer.br@gmail.com");
//    mailSender.setPassword("grrshjnpexvltzko");
//
//    Properties props = mailSender.getJavaMailProperties();
//    props.put("mail.transport.protocol", "smtp");
//    props.put("mail.smtp.auth", "true");
//    props.put("mail.smtp.starttls.enable", "true");
//    props.put("mail.debug", "true");
//
//    return mailSender;
//}
    @Bean
    public SimpleMailMessage templateSimpleMessage() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText("This is the test email template for your email:\n%s\n");
        return message;
    }
//
//    @Bean
//    public SpringTemplateEngine thymeleafTemplateEngine(ITemplateResolver templateResolver) {
//        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
//        templateEngine.setTemplateResolver(templateResolver);
//        templateEngine.setTemplateEngineMessageSource(emailMessageSource());
//        return templateEngine;
//    }
//
//    @Bean
//    public ITemplateResolver thymeleafClassLoaderTemplateResolver() {
//        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
//        templateResolver.setPrefix(mailTemplatesPath + "/");
//        templateResolver.setSuffix(".html");
//        templateResolver.setTemplateMode("HTML");
//        templateResolver.setCharacterEncoding("UTF-8");
//        return templateResolver;
//    }
//---------------------
//    @Bean
//    public ITemplateResolver thymeleafFilesystemTemplateResolver() {
//        FileTemplateResolver templateResolver = new FileTemplateResolver();
//        templateResolver.setPrefix(mailTemplatesPath + "/");
//        templateResolver.setSuffix(".html");
//        templateResolver.setTemplateMode("HTML");
//        templateResolver.setCharacterEncoding("UTF-8");
//        return templateResolver;
//    }

// -----------------------------

//    @Bean
//    public FreeMarkerConfigurer freemarkerClassLoaderConfig() {
//        Configuration configuration = new Configuration(Configuration.VERSION_2_3_27);
//        TemplateLoader templateLoader = new ClassTemplateLoader(this.getClass(), "/" + mailTemplatesPath);
//        configuration.setTemplateLoader(templateLoader);
//        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
//        freeMarkerConfigurer.setConfiguration(configuration);
//        return freeMarkerConfigurer;
//    }

// -----------------------------

//    @Bean
//    public FreeMarkerConfigurer freemarkerFilesystemConfig() throws IOException {
//        Configuration configuration = new Configuration(Configuration.VERSION_2_3_27);
//        TemplateLoader templateLoader = new FileTemplateLoader(new File(mailTemplatesPath));
//        configuration.setTemplateLoader(templateLoader);
//        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
//        freeMarkerConfigurer.setConfiguration(configuration);
//        return freeMarkerConfigurer;
//    }

// -----------------------------

    @Bean
    public ResourceBundleMessageSource emailMessageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("mailMessages");
        return messageSource;
    }

}