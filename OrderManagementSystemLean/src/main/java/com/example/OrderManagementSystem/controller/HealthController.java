package com.example.OrderManagementSystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HealthController {
    @GetMapping("/health")
    public ResponseEntity<String> health(){ return ResponseEntity.ok("OK"); }
}
