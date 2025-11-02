package com.example.OrderManagementSystem.config;

import com.example.OrderManagementSystem.model.*;
import com.example.OrderManagementSystem.repository.InMemoryStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreConfig {

    @Bean public InMemoryStore<Customer> customerStore() { return new InMemoryStore<>(); }
    @Bean public InMemoryStore<Order> orderStore() { return new InMemoryStore<>(); }
    @Bean public InMemoryStore<Contract> contractStore() { return new InMemoryStore<>(); }
    @Bean public InMemoryStore<SellableItem> itemStore() { return new InMemoryStore<>(); }
    @Bean public InMemoryStore<UnitOfMeasure> unitStore() { return new InMemoryStore<>(); }
}
