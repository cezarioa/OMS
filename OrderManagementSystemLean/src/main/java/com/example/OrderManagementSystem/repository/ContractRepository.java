package com.example.OrderManagementSystem.repository;

import com.example.OrderManagementSystem.model.Contract;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ContractRepository implements CrudRepository<Contract, Long> {
    private final InMemoryStore<Contract> store;
    public ContractRepository(InMemoryStore<Contract> contractStore){ this.store = contractStore; }

    @Override public List<Contract> findAll(){ return store.values(); }
    @Override public Optional<Contract> findById(Long id){ return Optional.ofNullable(store.get(id)); }
    @Override public Contract save(Contract e){
        if(e.getId()==null) e.setId(store.nextId());
        store.put(e.getId(), e);
        return e;
    }
    @Override public boolean deleteById(Long id){ return store.remove(id) != null; }
}
