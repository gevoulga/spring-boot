package ch.voulgarakis.recruitment.tests.config;

import java.io.IOException;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import ch.voulgarakis.icsc2018.ApplicationConfig;
import ch.voulgarakis.icsc2018.recruitment.service.RecruitmentService;
import ch.voulgarakis.icsc2018.recruitment.service.RecruitmentServiceImpl;

@SpringBootConfiguration
@ComponentScan(basePackageClasses = ApplicationConfig.class)
// @Profile("rest")
public class TestConfig {
    @Bean
    public RecruitmentService recruitmentService() {
        return new RecruitmentServiceImpl();
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate rest = new RestTemplate();
        rest.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false; // Never throw exception for any HTTP error code...!
            }
        });
        return rest;
    }

    // @Bean
    // public ChatController chatController() {
    // return new ChatController();
    // }

    // @Bean
    // public MatchController matchController() {
    // return new MatchController();
    // }
}
