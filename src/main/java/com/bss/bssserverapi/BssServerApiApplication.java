package com.bss.bssserverapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BssServerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BssServerApiApplication.class, args);
    }

}
