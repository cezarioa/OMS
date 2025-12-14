package com.example.OrderManagementSystem.repository;

import com.example.OrderManagementSystem.model.Contract;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long>, JpaSpecificationExecutor<Contract> {
    @Override
    @EntityGraph(attributePaths = {"contractType", "contractLines"})
    Optional<Contract> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"contractType", "contractLines"})
    List<Contract> findAll();

    @Override
    @EntityGraph(attributePaths = {"contractType", "contractLines"})
    List<Contract> findAll(org.springframework.data.jpa.domain.Specification<Contract> spec,
            org.springframework.data.domain.Sort sort);
}
