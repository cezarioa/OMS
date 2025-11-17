package com.example.OrderManagementSystem.repository;

import com.example.OrderManagementSystem.model.Customer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("file")
public class CustomerRepository extends InFileRepository<Customer> {
    public CustomerRepository() {
        super("data/customers.json", Customer.class);
    }
}

