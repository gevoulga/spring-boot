package ch.voulgarakis.recruitment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ch.voulgarakis.recruitment.controller.ChatController;
import ch.voulgarakis.recruitment.service.RecruitmentService;
import ch.voulgarakis.recruitment.service.RecruitmentServiceImpl;

@SpringBootApplication
public class ApplicationConfig {
    Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    @Bean
    public RecruitmentService recruitmentService() {
        return new RecruitmentServiceImpl();
    }

    @Bean
    public ChatController chatController() {
        return new ChatController();
    }

    public static void main(String[] args) {
        // Tell server to look for web-server.properties or web-server.yml
        // System.setProperty("spring.config.name", "web-server");
        SpringApplication.run(ApplicationConfig.class, args);
    }
}
