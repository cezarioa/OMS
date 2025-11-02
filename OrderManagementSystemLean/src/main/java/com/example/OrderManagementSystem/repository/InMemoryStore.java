package com.example.OrderManagementSystem.repository;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStore<T> {
    private final Map<Long, T> map = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public Long nextId() {
        return seq.getAndIncrement();
    }

    public void put(Long id, T value) {
        map.put(id, value);
    }

    public T get(Long id) {
        return map.get(id);
    }

    public T remove(Long id) {
        return map.remove(id);
    }

    public List<T> values() {
        return new ArrayList<>(map.values());
    }
}
