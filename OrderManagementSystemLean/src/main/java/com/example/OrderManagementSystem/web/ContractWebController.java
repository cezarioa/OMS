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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.beans.PropertyEditorSupport;

@Controller
@RequestMapping("/contracts")
public class ContractWebController {

    private final ContractService contractService;
    private final ContractTypeService contractTypeService;

    public ContractWebController(ContractService contractService, ContractTypeService contractTypeService) {
        this.contractService = contractService;
        this.contractTypeService = contractTypeService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(ContractStatus.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                for (ContractStatus status : ContractStatus.values()) {
                    if (status.toString().equals(text) || status.name().equals(text)) {
                        setValue(status);
                        return;
                    }
                }
                throw new IllegalArgumentException("Unknown status: " + text);
            }
        });
    }

    @GetMapping
    public String listContracts(Model model) {
        model.addAttribute("contracts", contractService.findAll());
        return "contracts/index";
    }

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
    public String saveContract(@Valid @ModelAttribute("contract") Contract formContract,
                               BindingResult bindingResult,
                               Model model) {

        if (formContract.getContractType() == null || formContract.getContractType().getId() == null) {
            bindingResult.rejectValue("contractType.id", "NotNull", "Please select a contract type.");
        }

        if (bindingResult.hasErrors()) {
            // Re-populate model for the form view if there are errors
            if (formContract.getContractType() == null) {
                formContract.setContractType(new ContractType());
            }
            model.addAttribute("contract", formContract);
            model.addAttribute("contractTypes", contractTypeService.findAll());
            model.addAttribute("statusOptions", ContractStatus.values());
            return "contracts/form";
        }

        // Validate that the contract type actually exists
        var typeOptional = contractTypeService.findById(formContract.getContractType().getId());
        if (typeOptional.isEmpty()) {
            bindingResult.rejectValue("contractType.id", "NotFound", "Selected contract type does not exist.");
            model.addAttribute("contract", formContract);
            model.addAttribute("contractTypes", contractTypeService.findAll());
            model.addAttribute("statusOptions", ContractStatus.values());
            return "contracts/form";
        }
        formContract.setContractType(typeOptional.get());

        Contract contractToSave;

        // CRITICAL FIX: Handle Updates vs Inserts
        if (formContract.getId() != null) {
            // UPDATE: Fetch existing from DB
            Contract existingContract = contractService.findById(formContract.getId())
                    .orElseThrow(() -> new RuntimeException("Contract not found"));

            // Update only editable fields
            existingContract.setName(formContract.getName());
            existingContract.setStatus(formContract.getStatus());
            existingContract.setContractType(formContract.getContractType());

            contractToSave = existingContract;
        } else {
            // CREATE: Use the new object directly
            contractToSave = formContract;
        }

        Contract savedContract = contractService.save(contractToSave);
        return "redirect:/contracts/" + savedContract.getId();
    }

    @PostMapping("/{id}/delete")
    public String deleteContract(@PathVariable("id") Long id) {
        contractService.deleteById(id);
        return "redirect:/contracts";
    }
}