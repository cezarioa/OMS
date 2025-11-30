package com.example.OrderManagementSystem.service;

import com.example.OrderManagementSystem.model.Order;
import com.example.OrderManagementSystem.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository repo;
    public OrderService(OrderRepository repo){ this.repo = repo; }
    public List<Order> findAll(){ return repo.findAll(); }
    public Optional<Order> findById(Long id){ return repo.findById(id); }
    public Order save(Order e){ return repo.save(e); }
    public void deleteById(Long id){ repo.deleteById(id); }
}
