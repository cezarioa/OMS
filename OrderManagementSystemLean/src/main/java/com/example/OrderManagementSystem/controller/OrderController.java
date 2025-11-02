package com.example.OrderManagementSystem.controller;

import com.example.OrderManagementSystem.model.Order;
import com.example.OrderManagementSystem.model.OrderLine;
import com.example.OrderManagementSystem.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService service;
    public OrderController(OrderService service){ this.service = service; }

    @GetMapping
    public ResponseEntity<List<Order>> all(){ return ResponseEntity.ok(service.findAll()); }

    @GetMapping("/{id}")
    public ResponseEntity<Order> one(@PathVariable Long id){
        Optional<Order> o = service.findById(id);
        return o.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order o){
        Order saved = service.save(o);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/{id}/lines")
    public ResponseEntity<Order> addLine(@PathVariable Long id, @RequestBody OrderLine line){
        Optional<Order> maybe = service.findById(id);
        if(maybe.isEmpty()) return ResponseEntity.notFound().build();
        Order o = maybe.get();
        o.addOrderLine(line);
        return ResponseEntity.ok(service.save(o));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        return service.deleteById(id) ? ResponseEntity.noContent().build()
                                      : ResponseEntity.notFound().build();
    }
}
