package de.ctimm

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.client.RestTemplate

@SpringBootApplication
@EnableScheduling
class FacturasApplication {

    @Bean
    public RestTemplate restTEmplate() {
        new RestTemplate()
    }

    static void main(String[] args) {
        SpringApplication.run FacturasApplication, args
    }
}
