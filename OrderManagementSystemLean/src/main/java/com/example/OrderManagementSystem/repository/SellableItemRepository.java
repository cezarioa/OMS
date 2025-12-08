package com.example.OrderManagementSystem.repository;

import com.example.OrderManagementSystem.model.SellableItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellableItemRepository extends JpaRepository<SellableItem, Long> {
}