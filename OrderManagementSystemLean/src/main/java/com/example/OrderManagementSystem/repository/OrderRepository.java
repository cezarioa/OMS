package com.example.OrderManagementSystem.repository;

import com.example.OrderManagementSystem.model.Order;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository implements CrudRepository<Order, Long> {
    private final InMemoryStore<Order> store;
    public OrderRepository(InMemoryStore<Order> orderStore){ this.store = orderStore; }

    @Override public List<Order> findAll(){ return store.values(); }
    @Override public Optional<Order> findById(Long id){ return Optional.ofNullable(store.get(id)); }
    @Override public Order save(Order e){
        if(e.getId()==null) e.setId(store.nextId());
        store.put(e.getId(), e);
        return e;
    }
    @Override public boolean deleteById(Long id){ return store.remove(id) != null; }
}
