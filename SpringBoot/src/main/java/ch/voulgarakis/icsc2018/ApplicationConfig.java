package ch.voulgarakis.icsc2018;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ch.voulgarakis.icsc2018.recruitment.service.RecruitmentService;
import ch.voulgarakis.icsc2018.recruitment.service.RecruitmentServiceImpl;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableTransactionManagement
// @ComponentScan(excludeFilters = { @Filter(type = FilterType.ANNOTATION, classes = SpringBootConfiguration.class),
// @Filter(type = FilterType.ANNOTATION, classes = EnableAutoConfiguration.class),
// @Filter(type = FilterType.ANNOTATION, classes = SpringBootApplication.class) })
public class ApplicationConfig {
    Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    @Bean
    public RecruitmentService recruitmentService() {
        return new RecruitmentServiceImpl();
    }

    // @Bean
    // public ChatController chatController() {
    // return new ChatController();
    // }
    //
    // @Bean
    // public MatchController matchController() {
    // return new MatchController();
    // }

    public static void main(String[] args) {
        SpringApplication.run(ApplicationConfig.class, args);
    }
}
