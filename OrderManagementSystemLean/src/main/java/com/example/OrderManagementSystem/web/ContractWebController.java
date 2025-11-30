package com.example.OrderManagementSystem.web;

import com.example.OrderManagementSystem.model.Contract;
import com.example.OrderManagementSystem.model.Contract.ContractStatus;
import com.example.OrderManagementSystem.model.ContractType;
import com.example.OrderManagementSystem.service.ContractService;
import com.example.OrderManagementSystem.service.ContractTypeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        Contract contract = new Contract();
        contract.setContractType(new ContractType());
        model.addAttribute("contract", contract);
        model.addAttribute("contractTypes", contractTypeService.findAll());
        model.addAttribute("statusOptions", ContractStatus.values());
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
        if (contract.getContractType() == null) {
            contract.setContractType(new ContractType());
        }
        model.addAttribute("contract", contract);
        model.addAttribute("contractTypes", contractTypeService.findAll());
        model.addAttribute("statusOptions", ContractStatus.values());
        return "contracts/form";
    }

    @PostMapping
    public String saveContract(@Valid @ModelAttribute("contract") Contract contract,
                               BindingResult bindingResult,
                               Model model) {
        if (contract.getContractType() == null || contract.getContractType().getId() == null) {
            bindingResult.rejectValue("contractType.id", "NotNull", "Please select a contract type.");
        }

        if (bindingResult.hasErrors()) {
            if (contract.getContractType() == null) {
                contract.setContractType(new ContractType());
            }
            model.addAttribute("contract", contract);
            model.addAttribute("contractTypes", contractTypeService.findAll());
            model.addAttribute("statusOptions", ContractStatus.values());
            return "contracts/form";
        }

        var typeOptional = contractTypeService.findById(contract.getContractType().getId());
        if (typeOptional.isEmpty()) {
            bindingResult.rejectValue("contractType.id", "NotFound", "Selected contract type does not exist.");
            model.addAttribute("contract", contract);
            model.addAttribute("contractTypes", contractTypeService.findAll());
            model.addAttribute("statusOptions", ContractStatus.values());
            return "contracts/form";
        }
        contract.setContractType(typeOptional.get());

        Contract savedContract = contractService.save(contract);
        return "redirect:/contracts/" + savedContract.getId();
    }

    @PostMapping("/{id}/delete")
    public String deleteContract(@PathVariable("id") Long id) {
        contractService.deleteById(id);
        return "redirect:/contracts";
    }
}
