package ru.shubert.jobportal.config;

import org.apache.wicket.protocol.http.WebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import ru.shubert.jobportal.web.JobPortalWebApplication;

/**
 * http://blog.springsource.com/2011/06/10/spring-3-1-m2-configuration-enhancements/
 */

@Configuration
@ComponentScan("ru.shubert.jobportal")
public class AppConfig {


    @Bean
    public WebApplication application() {
        return new JobPortalWebApplication();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
