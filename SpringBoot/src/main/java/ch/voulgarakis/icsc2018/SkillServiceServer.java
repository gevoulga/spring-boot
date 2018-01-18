package ch.voulgarakis.icsc2018;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import ch.voulgarakis.icsc2018.recruitment.controller.CRUDSkillController;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
// @EnableAutoConfiguration
// @Import(AccountsWebApplication.class)
public class SkillServiceServer {
    @LoadBalanced // Explicitly request the load-balanced template with Ribbon built-in
    @Bean // NO LONGER auto-created by Spring Cloud (see below)
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    private CRUDSkillController recruitmentService() {
        return new CRUDSkillController();
    }

    public static void main(String[] args) {
        // Will configure using accounts-server.yml
        System.setProperty("spring.config.name", "microservice");
        System.setProperty("spring.cloud.bootstrap.name", "skill-service-bootstrap");

        SpringApplication.run(SkillServiceServer.class, args);
    }
}