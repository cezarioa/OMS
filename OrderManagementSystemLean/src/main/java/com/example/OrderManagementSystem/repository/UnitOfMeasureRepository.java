package com.example.OrderManagementSystem.repository;

import com.example.OrderManagementSystem.model.UnitOfMeasure;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("file")
public class UnitOfMeasureRepository extends InFileRepository<UnitOfMeasure> {
    public UnitOfMeasureRepository() {
        super("data/unitOfMeasures.json", UnitOfMeasure.class);
    }
}

