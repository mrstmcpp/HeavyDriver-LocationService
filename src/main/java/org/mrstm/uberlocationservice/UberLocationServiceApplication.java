package org.mrstm.uberlocationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;


@SpringBootApplication
@EnableDiscoveryClient
public class UberLocationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UberLocationServiceApplication.class, args);
    }

}
