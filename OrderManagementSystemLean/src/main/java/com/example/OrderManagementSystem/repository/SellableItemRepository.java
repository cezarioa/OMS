package com.example.OrderManagementSystem.repository;

import com.example.OrderManagementSystem.model.SellableItem;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SellableItemRepository implements CrudRepository<SellableItem, Long> {
    private final InMemoryStore<SellableItem> store;
    public SellableItemRepository(InMemoryStore<SellableItem> itemStore){ this.store = itemStore; }

    @Override public List<SellableItem> findAll(){ return store.values(); }
    @Override public Optional<SellableItem> findById(Long id){ return Optional.ofNullable(store.get(id)); }
    @Override public SellableItem save(SellableItem e){
        if(e.getId()==null) e.setId(store.nextId());
        store.put(e.getId(), e);
        return e;
    }
    @Override public boolean deleteById(Long id){ return store.remove(id) != null; }
}
