package my.project.fer.ryzetello.ryzetellowebserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@EnableIntegration
public class RyzetelloWebserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(RyzetelloWebserverApplication.class, args);
    }

}
