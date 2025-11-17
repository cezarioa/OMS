package com.example.OrderManagementSystem.repository;

import com.example.OrderManagementSystem.model.ContractType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("file")
public class ContractTypeRepository extends InFileRepository<ContractType> {
    public ContractTypeRepository() {
        super("data/contractTypes.json", ContractType.class);
    }
}

