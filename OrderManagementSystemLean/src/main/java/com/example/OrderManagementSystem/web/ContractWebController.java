package com.example.OrderManagementSystem.web;

import com.example.OrderManagementSystem.model.Contract;
import com.example.OrderManagementSystem.service.ContractService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contracts")
public class ContractWebController {

    private final ContractService contractService;

    public ContractWebController(ContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping
    public String listContracts(Model model) {
        model.addAttribute("contracts", contractService.findAll());
        return "contracts/index";
    }

    @GetMapping("/new")
    public String showNewContractForm(Model model) {
        model.addAttribute("contract", new Contract());
        return "contracts/form";
    }

    @PostMapping
    public String saveContract(@ModelAttribute Contract contract) {
        contractService.save(contract);
        return "redirect:/contracts";
    }

    @PostMapping("/{id}/delete")
    public String deleteContract(@PathVariable("id") Long id) {
        contractService.deleteById(id);
        return "redirect:/contracts";
    }
}


