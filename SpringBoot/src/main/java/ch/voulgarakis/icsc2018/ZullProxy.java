package ch.voulgarakis.icsc2018;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableZuulProxy
public class ZullProxy {

    public static void main(String[] args) {
        SpringApplication.run(ZullProxy.class, args);
    }
}