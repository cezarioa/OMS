package com.example.OrderManagementSystem.controller;

import com.example.OrderManagementSystem.model.Contract;
import com.example.OrderManagementSystem.model.ContractLine;
import com.example.OrderManagementSystem.service.ContractService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contracts")
@CrossOrigin(origins = "*")
public class ContractController {

    private final ContractService service;
    public ContractController(ContractService service){ this.service = service; }

    @GetMapping
    public ResponseEntity<List<Contract>> all(){ return ResponseEntity.ok(service.findAll()); }

    @GetMapping("/{id}")
    public ResponseEntity<Contract> one(@PathVariable Long id){
        Optional<Contract> c = service.findById(id);
        return c.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Contract> create(@RequestBody Contract c){
        Contract saved = service.save(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/{id}/lines")
    public ResponseEntity<Contract> addLine(@PathVariable Long id, @RequestBody ContractLine line){
        Optional<Contract> maybe = service.findById(id);
        if(maybe.isEmpty()) return ResponseEntity.notFound().build();
        Contract c = maybe.get();
        c.addContractLine(line);
        return ResponseEntity.ok(service.save(c));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Contract> updateStatus(@PathVariable Long id, @RequestBody Contract req){
        Optional<Contract> maybe = service.findById(id);
        if(maybe.isEmpty()) return ResponseEntity.notFound().build();
        Contract c = maybe.get();
        if("Active".equalsIgnoreCase(req.getStatus())) c.activate();
        else if("Down".equalsIgnoreCase(req.getStatus())) c.deactivate();
        else if(req.getStatus()!=null) c.setStatus(req.getStatus());
        return ResponseEntity.ok(service.save(c));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        return service.deleteById(id) ? ResponseEntity.noContent().build()
                                      : ResponseEntity.notFound().build();
    }
}
