package no.webstep.ai.mcp.tests;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@SpringBootApplication
public class StartLocal {

    @Bean
    @ConditionalOnMissingBean(Clock.class)
    public Clock defClock() {
        return Clock.systemDefaultZone();
    }

     public static void main(String[] args) {
        SpringApplication.run(StartLocal.class, args);
    }
}