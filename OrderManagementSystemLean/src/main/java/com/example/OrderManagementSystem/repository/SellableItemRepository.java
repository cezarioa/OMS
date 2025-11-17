package com.example.OrderManagementSystem.repository;

import com.example.OrderManagementSystem.model.SellableItem;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("file")
public class SellableItemRepository extends InFileRepository<SellableItem> {
    public SellableItemRepository() {
        super("data/sellableItems.json", SellableItem.class);
    }
}

