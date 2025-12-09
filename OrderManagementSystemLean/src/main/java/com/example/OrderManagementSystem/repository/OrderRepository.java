package com.example.OrderManagementSystem.repository;

import java.util.Optional;
import java.util.List;

import com.example.OrderManagementSystem.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Override
    @EntityGraph(attributePaths = {"customer", "contract", "orderLines"})
    Optional<Order> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"customer", "contract", "orderLines"})
    List<Order> findAll();

    void deleteByName(String name);
}
