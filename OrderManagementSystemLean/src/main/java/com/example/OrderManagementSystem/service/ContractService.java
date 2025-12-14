package com.example.OrderManagementSystem.service;

import com.example.OrderManagementSystem.model.Contract;
import com.example.OrderManagementSystem.repository.ContractRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContractService {
    private final ContractRepository repo;

    public ContractService(ContractRepository repo){ this.repo = repo; }

    public List<Contract> findAll(){ return repo.findAll(); }

    public Optional<Contract> findById(Long id){ return repo.findById(id); }

    public Contract save(Contract e){ return repo.save(e); }

    public void deleteById(Long id){ repo.deleteById(id); }

    public List<Contract> searchContracts(Specification<Contract> spec, Sort sort) {
        if (spec == null) {
            return repo.findAll(sort);
        }
        return repo.findAll(spec, sort);
    }
}
