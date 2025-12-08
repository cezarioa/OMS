package com.example.OrderManagementSystem.repository;

import com.example.OrderManagementSystem.model.UnitOfMeasure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitOfMeasureRepository extends JpaRepository<UnitOfMeasure, Long> {
    UnitOfMeasure findByName(String name);
}