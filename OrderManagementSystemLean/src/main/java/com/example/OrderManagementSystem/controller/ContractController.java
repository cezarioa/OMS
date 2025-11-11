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

        Contract contractToUpdate = maybe.get();

        // Get the new status from the request body.
        // Spring/Jackson will deserialize the JSON string ("ACTIVE" or "DOWN")
        // into the Contract.ContractStatus enum.
        Contract.ContractStatus newStatus = req.getStatus();

        // Check if a new status was provided in the request
        if(newStatus != null) {
            // Set the status on the contract retrieved from the database
            contractToUpdate.setStatus(newStatus);
        }

        return ResponseEntity.ok(service.save(contractToUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id){
        return service.deleteById(id) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}