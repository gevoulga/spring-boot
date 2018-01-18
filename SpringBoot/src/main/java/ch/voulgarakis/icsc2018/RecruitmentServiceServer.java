package ch.voulgarakis.icsc2018;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.client.RestTemplate;

import ch.voulgarakis.icsc2018.recruitment.config.CustomPropertySourceLocator;
import ch.voulgarakis.icsc2018.recruitment.service.RecruitmentMicroservice;
import ch.voulgarakis.icsc2018.recruitment.service.RecruitmentService;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan(excludeFilters = { @Filter(type = FilterType.ANNOTATION, classes = SpringBootConfiguration.class),
        @Filter(type = FilterType.ANNOTATION, classes = EnableAutoConfiguration.class),
        @Filter(type = FilterType.ANNOTATION, classes = SpringBootApplication.class) })
public class RecruitmentServiceServer {
    @LoadBalanced // Explicitly request the load-balanced template with Ribbon built-in
    @Bean // NO LONGER auto-created by Spring Cloud (see below)
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RecruitmentService recruitmentService() {
        return new RecruitmentMicroservice();
    }

    @Bean
    public PropertySourceLocator propertySourceLocator() {
        return new CustomPropertySourceLocator();
    }

    public static void main(String[] args) {
        // Will configure using accounts-server.yml
        System.setProperty("spring.config.name", "microservice");
        System.setProperty("spring.cloud.bootstrap.name", "microservice-bootstrap");

        SpringApplication.run(RecruitmentServiceServer.class, args);
    }
}