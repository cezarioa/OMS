package com.example.OrderManagementSystem.web;

import com.example.OrderManagementSystem.model.Contract;
import com.example.OrderManagementSystem.service.ContractService;
// --- IMPORT THE NEW SERVICE ---
import com.example.OrderManagementSystem.service.ContractTypeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contracts")
public class ContractWebController {

    private final ContractService contractService;
    // --- ADD THE NEW SERVICE FIELD ---
    private final ContractTypeService contractTypeService;

    // --- UPDATE THE CONSTRUCTOR ---
    public ContractWebController(ContractService contractService, ContractTypeService contractTypeService) {
        this.contractService = contractService;
        this.contractTypeService = contractTypeService; // Assign it
    }

    @GetMapping
    public String listContracts(Model model) {
        model.addAttribute("contracts", contractService.findAll());
        return "contracts/index";
    }

    @GetMapping("/new")
    public String showNewContractForm(Model model) {
        model.addAttribute("contract", new Contract());
        // --- ADD THE LIST OF CONTRACT TYPES TO THE MODEL ---
        model.addAttribute("contractTypes", contractTypeService.findAll());
        return "contracts/form";
    }

    @PostMapping
    public String saveContract(@ModelAttribute Contract contract) {
        contractService.save(contract);
        return "redirect:/contracts";
    }

    @PostMapping("/{id}/delete")
    public String deleteContract(@PathVariable Long id) {
        contractService.deleteById(id);
        return "redirect:/contracts";
    }
}