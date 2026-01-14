package com.example.lineofduty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LineofdutyApplication {

    public static void main(String[] args) {
        SpringApplication.run(LineofdutyApplication.class, args);
    }

}
