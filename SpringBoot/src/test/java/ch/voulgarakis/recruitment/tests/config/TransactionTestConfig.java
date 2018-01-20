package ch.voulgarakis.recruitment.tests.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import ch.voulgarakis.icsc2018.ApplicationConfig;
import ch.voulgarakis.icsc2018.recruitment.service.RecruitmentService;
import ch.voulgarakis.icsc2018.recruitment.service.TransactionFailRecruitmentServiceImpl;

@ComponentScan(basePackageClasses = ApplicationConfig.class)
// @Profile("rest")
public class TransactionTestConfig {
    @Bean
    public RecruitmentService recruitmentService() {
        return new TransactionFailRecruitmentServiceImpl();
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
}
