package com.example.OrderManagementSystem.repository;

import com.example.OrderManagementSystem.model.UnitOfMeasure;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UnitOfMeasureRepository implements CrudRepository<UnitOfMeasure, Long> {
    private final InMemoryStore<UnitOfMeasure> store;

    public UnitOfMeasureRepository(InMemoryStore<UnitOfMeasure> unitStore){
        this.store = unitStore;
        initDefaults();
    }

    private void initDefaults(){
        save(new UnitOfMeasure(null, "Piece", "pc"));
        save(new UnitOfMeasure(null, "Hour", "h"));
    }

    @Override public List<UnitOfMeasure> findAll(){ return store.values(); }
    @Override public Optional<UnitOfMeasure> findById(Long id){ return Optional.ofNullable(store.get(id)); }
    public Optional<UnitOfMeasure> findBySymbol(String s){
        return store.values().stream().filter(u -> u.getSymbol()!=null && u.getSymbol().equalsIgnoreCase(s)).findFirst();
    }
    @Override public UnitOfMeasure save(UnitOfMeasure e){
        if(e.getId()==null) e.setId(store.nextId());
        store.put(e.getId(), e);
        return e;
    }
    @Override public boolean deleteById(Long id){ return store.remove(id) != null; }
}
