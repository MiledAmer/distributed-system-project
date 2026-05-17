package org.project;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public ManagedChannel orderChannel() {
        return ManagedChannelBuilder
                .forAddress("order-service", 50051)
                .usePlaintext()
                .build();
    }

    @Bean
    public ManagedChannel kitchenChannel() {
        return ManagedChannelBuilder
                .forAddress("kitchen-service", 50052)
                .usePlaintext()
                .build();
    }

    @Bean
    public ManagedChannel accountChannel() {
        return ManagedChannelBuilder
                .forAddress("account-service", 50053)
                .usePlaintext()
                .build();
    }
}