package ch.voulgarakis.recruitment.tests.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;

import ch.voulgarakis.recruitment.ApplicationConfig;

@SpringBootConfiguration
@ComponentScan(basePackageClasses = ApplicationConfig.class)
public class TestConfig {
    // @Bean
    // public RecruitmentService recruitmentService() {
    // return new RecruitmentServiceImpl();
    // }

    // @Bean
    // public ChatController chatController() {
    // return new ChatController();
    // }

    // @Bean
    // public MatchController matchController() {
    // return new MatchController();
    // }
}
