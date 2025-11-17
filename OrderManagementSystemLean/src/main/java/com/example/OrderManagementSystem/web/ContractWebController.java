package com.example.OrderManagementSystem.web;

import com.example.OrderManagementSystem.model.Contract;
import com.example.OrderManagementSystem.service.ContractService;
import com.example.OrderManagementSystem.service.ContractTypeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contracts")
public class ContractWebController {

    private final ContractService contractService;
    private final ContractTypeService contractTypeService;

    public ContractWebController(ContractService contractService, ContractTypeService contractTypeService) {
        this.contractService = contractService;
        this.contractTypeService = contractTypeService; // Assign it
    }

    @GetMapping
    public String listContracts(Model model) {
        model.addAttribute("contracts", contractService.findAll());
        return "contracts/index";
    }

    /**
     * GET /contracts/{id}
     * Shows the details page for an existing contract. (NEW)
     */
    @GetMapping("/{id}")
    public String getContractDetails(@PathVariable("id") Long id, Model model) {
        Contract contract = contractService.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found with ID: " + id));
        model.addAttribute("contract", contract);
        return "contracts/details";
    }

    @GetMapping("/new")
    public String showNewContractForm(Model model) {
        model.addAttribute("contract", new Contract());
        model.addAttribute("contractTypes", contractTypeService.findAll());
        return "contracts/form";
    }

    /**
     * GET /contracts/{id}/edit
     * Shows the populated form to edit an existing contract. (NEW)
     */
    @GetMapping("/{id}/edit")
    public String showEditContractForm(@PathVariable("id") Long id, Model model) {
        Contract contract = contractService.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found with ID: " + id));
        model.addAttribute("contract", contract);
        model.addAttribute("contractTypes", contractTypeService.findAll());
        return "contracts/form";
    }

    @PostMapping
    public String saveContract(@ModelAttribute Contract contract) {
        Contract savedContract = contractService.save(contract);
        // Redirect to the details page of the saved/updated contract
        return "redirect:/contracts/" + savedContract.getId();
    }

    @PostMapping("/{id}/delete")
    public String deleteContract(@PathVariable("id") Long id) {
        contractService.deleteById(id);
        return "redirect:/contracts";
    }
}