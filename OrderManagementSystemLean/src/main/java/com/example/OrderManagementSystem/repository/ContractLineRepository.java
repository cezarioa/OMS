package com.example.OrderManagementSystem.repository;

import com.example.OrderManagementSystem.model.ContractLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractLineRepository extends JpaRepository<ContractLine, Long> {
    List<ContractLine> findByContractId(Long contractId);
}