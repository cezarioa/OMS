package com.example.OrderManagementSystem.repository;

import com.example.OrderManagementSystem.model.ContractType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ContractTypeRepository implements CrudRepository<ContractType, Long> {

    private final InMemoryStore<ContractType> store;

    public ContractTypeRepository(InMemoryStore<ContractType> contractTypeStore) {
        this.store = contractTypeStore;
        // Add some default data
        initDefaults();
    }

    private void initDefaults() {
        save(new ContractType(null, "Standard Customer Contract", "Customer"));
        save(new ContractType(null, "Premium Service Contract", "Customer"));
        save(new ContractType(null, "Default Vendor Agreement", "Seller"));
    }

    @Override
    public List<ContractType> findAll() { return store.values(); }

    @Override
    public Optional<ContractType> findById(Long id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public ContractType save(ContractType e) {
        if (e.getId() == null) e.setId(store.nextId());
        store.put(e.getId(), e);
        return e;
    }

    @Override
    public boolean deleteById(Long id) { return store.remove(id) != null; }
}