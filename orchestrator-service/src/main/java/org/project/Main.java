package org.project;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.project.client.*;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    // ---------------- CHANNELS ----------------

    @Bean
    @Qualifier("orderChannel")
    public ManagedChannel orderChannel() {
        return ManagedChannelBuilder
                .forAddress("order-service", 50051)
                .usePlaintext()
                .build();
    }

    @Bean
    @Qualifier("kitchenChannel")
    public ManagedChannel kitchenChannel() {
        return ManagedChannelBuilder
                .forAddress("kitchen-service", 50052)
                .usePlaintext()
                .build();
    }

    @Bean
    @Qualifier("accountChannel")
    public ManagedChannel accountChannel() {
        return ManagedChannelBuilder
                .forAddress("account-service", 50053)
                .usePlaintext()
                .build();
    }

    // ---------------- CLIENTS ----------------

    @Bean
    public OrderClient orderClient(@Qualifier("orderChannel") ManagedChannel orderChannel) {
        return new OrderClient(orderChannel);
    }

    @Bean
    public KitchenClient kitchenClient(@Qualifier("kitchenChannel") ManagedChannel kitchenChannel) {
        return new KitchenClient(kitchenChannel);
    }

    @Bean
    public AccountClient accountClient(@Qualifier("accountChannel") ManagedChannel accountChannel) {
        return new AccountClient(accountChannel);
    }
}