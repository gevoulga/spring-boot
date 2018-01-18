package ch.voulgarakis.icsc2018;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableEurekaServer
public class EurekaServer {

    public static void main(String[] args) {
        // Tell Boot to look for registration-server.yml
        System.setProperty("spring.config.name", "eureka");
        System.setProperty("spring.cloud.bootstrap.name", "eureka-bootstrap");

        SpringApplication.run(EurekaServer.class, args);
    }
}