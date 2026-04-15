package org.example.tdd_lab3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class TddLab3Application {

    public static void main(String[] args) {
        SpringApplication.run(TddLab3Application.class, args);
    }

}
