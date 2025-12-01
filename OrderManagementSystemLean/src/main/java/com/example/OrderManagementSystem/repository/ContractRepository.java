package com.example.OrderManagementSystem.repository;

import java.util.List;
import java.util.Optional;

import com.example.OrderManagementSystem.model.Contract;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    @Override
    @EntityGraph(attributePaths = {"contractType", "contractLines"})
    Optional<Contract> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"contractType"})
    List<Contract> findAll();
}
