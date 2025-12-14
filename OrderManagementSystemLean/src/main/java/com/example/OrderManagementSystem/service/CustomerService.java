package com.example.OrderManagementSystem.service;

import com.example.OrderManagementSystem.model.Customer;
import com.example.OrderManagementSystem.repository.CustomerRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository repo;

    public CustomerService(CustomerRepository repo){ this.repo = repo; }

    public List<Customer> findAll(){ return repo.findAll(); }

    public Optional<Customer> findById(Long id){ return repo.findById(id); }

    public Customer save(Customer e){ return repo.save(e); }

    public void deleteById(Long id){ repo.deleteById(id); }

    public List<Customer> searchCustomers(Specification<Customer> spec, Sort sort) {
        if (spec == null) {
            return repo.findAll(sort);
        }
        return repo.findAll(spec, sort);
    }
}
