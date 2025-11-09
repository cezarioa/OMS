package com.example.OrderManagementSystem.service;

import com.example.OrderManagementSystem.model.ContractType;
import com.example.OrderManagementSystem.repository.ContractTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContractTypeService {
    private final ContractTypeRepository repo;
    public ContractTypeService(ContractTypeRepository repo) { this.repo = repo; }
    public List<ContractType> findAll() { return repo.findAll(); }
    public Optional<ContractType> findById(Long id) { return repo.findById(id); }
    public ContractType save(ContractType e) { return repo.save(e); }
    public boolean deleteById(Long id) { return repo.deleteById(id); }
}