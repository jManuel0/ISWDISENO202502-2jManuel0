package com.voluntariado.plataforma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PlataformaVoluntariadoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlataformaVoluntariadoApplication.class, args);
    }
}
