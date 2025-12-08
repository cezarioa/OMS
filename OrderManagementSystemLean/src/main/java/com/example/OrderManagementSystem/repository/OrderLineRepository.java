package com.example.OrderManagementSystem.repository;

import com.example.OrderManagementSystem.model.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {
    List<OrderLine> findByOrderId(Long orderId);
}