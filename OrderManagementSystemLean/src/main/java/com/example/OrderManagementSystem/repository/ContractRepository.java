package com.example.OrderManagementSystem.repository;

import com.example.OrderManagementSystem.model.Contract;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("file")
public class ContractRepository extends InFileRepository<Contract> {
    public ContractRepository() {
        super("data/contracts.json", Contract.class);
    }
}

