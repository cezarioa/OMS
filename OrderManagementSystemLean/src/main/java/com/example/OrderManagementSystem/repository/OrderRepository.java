package com.example.OrderManagementSystem.repository;

import com.example.OrderManagementSystem.model.Order;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("file")
public class OrderRepository extends InFileRepository<Order> {
    public OrderRepository() {
        super("data/orders.json", Order.class);
    }
}

