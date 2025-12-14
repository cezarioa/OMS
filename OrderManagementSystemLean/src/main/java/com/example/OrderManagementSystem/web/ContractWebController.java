package com.example.OrderManagementSystem.web;

import com.example.OrderManagementSystem.model.Contract;
import com.example.OrderManagementSystem.model.Contract.ContractStatus;
import com.example.OrderManagementSystem.model.ContractLine;
import com.example.OrderManagementSystem.model.ContractType;
import com.example.OrderManagementSystem.model.SellableItem;
import com.example.OrderManagementSystem.model.UnitOfMeasure;
import com.example.OrderManagementSystem.repository.ContractSpecifications;
import com.example.OrderManagementSystem.service.ContractService;
import com.example.OrderManagementSystem.service.ContractTypeService;
import com.example.OrderManagementSystem.service.SellableItemService;
import com.example.OrderManagementSystem.service.UnitOfMeasureService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.beans.PropertyEditorSupport;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.util.StringUtils;

@Controller
@RequestMapping("/contracts")
public class ContractWebController {

    private static final int MIN_CONTRACT_LINES = 3;
    private static final Set<String> SORTABLE_FIELDS = Set.of("id", "name", "status", "contractType.name");

    private final ContractService contractService;
    private final ContractTypeService contractTypeService;
    private final SellableItemService sellableItemService;
    private final UnitOfMeasureService unitOfMeasureService;

    public ContractWebController(ContractService contractService,
                                 ContractTypeService contractTypeService,
                                 SellableItemService sellableItemService,
                                 UnitOfMeasureService unitOfMeasureService) {
        this.contractService = contractService;
        this.contractTypeService = contractTypeService;
        this.sellableItemService = sellableItemService;
        this.unitOfMeasureService = unitOfMeasureService;
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

        binder.registerCustomEditor(SellableItem.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.isBlank()) {
                    setValue(null);
                    return;
                }
                try {
                    Long id = Long.valueOf(text);
                    sellableItemService.findById(id).ifPresent(this::setValue);
                } catch (NumberFormatException ignored) {
                    setValue(null);
                }
            }
        });

        binder.registerCustomEditor(UnitOfMeasure.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.isBlank()) {
                    setValue(null);
                    return;
                }
                try {
                    Long id = Long.valueOf(text);
                    unitOfMeasureService.findById(id).ifPresent(this::setValue);
                } catch (NumberFormatException ignored) {
                    setValue(null);
                }
            }
        });
    }

    @GetMapping
    public String listContracts(@RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "status", required = false) String statusValue,
            @RequestParam(name = "contractType", required = false) String contractType,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {

        Sort sort = resolveSort(sortBy, sortDir, SORTABLE_FIELDS, "id");
        ContractStatus status = parseStatus(statusValue);
        Specification<Contract> spec = ContractSpecifications.withFilters(name, status, contractType);
        List<Contract> contracts = contractService.searchContracts(spec, sort);

        model.addAttribute("contracts", contracts);
        model.addAttribute("name", name);
        model.addAttribute("status", status);
        model.addAttribute("contractType", contractType);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortableFields", SORTABLE_FIELDS);
        model.addAttribute("statusOptions", ContractStatus.values());

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
        ensureMinimumContractLines(contract);
        prepareContractFormModel(model, contract);
        return "contracts/form";
    }

    @GetMapping("/{id}/edit")
    public String showEditContractForm(@PathVariable("id") Long id, Model model) {
        Contract contract = contractService.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found with ID: " + id));
        if (contract.getContractType() == null) {
            contract.setContractType(new ContractType());
        }
        ensureMinimumContractLines(contract);
        prepareContractFormModel(model, contract);
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
            if (formContract.getContractType() == null) {
                formContract.setContractType(new ContractType());
            }
            ensureMinimumContractLines(formContract);
            prepareContractFormModel(model, formContract);
            return "contracts/form";
        }

        // Validate that the contract type actually exists
        var typeOptional = contractTypeService.findById(formContract.getContractType().getId());
        if (typeOptional.isEmpty()) {
            bindingResult.rejectValue("contractType.id", "NotFound", "Selected contract type does not exist.");
            ensureMinimumContractLines(formContract);
            prepareContractFormModel(model, formContract);
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
        applyContractLines(contractToSave, formContract.getContractLines());

        Contract savedContract = contractService.save(contractToSave);
        return "redirect:/contracts/" + savedContract.getId();
    }

    @PostMapping("/{id}/delete")
    public String deleteContract(@PathVariable("id") Long id) {
        contractService.deleteById(id);
        return "redirect:/contracts";
    }

    private Sort resolveSort(String sortBy, String sortDir, Set<String> allowed, String fallback) {
        if (sortBy == null || !allowed.contains(sortBy)) {
            sortBy = fallback;
        }
        Sort sort = Sort.by(sortBy);
        return "desc".equalsIgnoreCase(sortDir) ? sort.descending() : sort.ascending();
    }

    private ContractStatus parseStatus(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return ContractStatus.valueOf(value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private void prepareContractFormModel(Model model, Contract contract) {
        model.addAttribute("contract", contract);
        model.addAttribute("contractTypes", contractTypeService.findAll());
        model.addAttribute("statusOptions", ContractStatus.values());
        model.addAttribute("sellableItems", sellableItemService.findAll());
        model.addAttribute("units", unitOfMeasureService.findAll());
    }

    private void ensureMinimumContractLines(Contract contract) {
        if (contract.getContractLines() == null) return;
        while (contract.getContractLines().size() < MIN_CONTRACT_LINES) {
            contract.addContractLine(new ContractLine());
        }
    }

    private void applyContractLines(Contract target, List<ContractLine> source) {
        target.getContractLines().clear();
        if (source == null) return;
        for (ContractLine line : source) {
            if (line == null) continue;
            Optional<SellableItem> item = resolveSellableItem(line);
            Optional<UnitOfMeasure> unit = resolveUnitOfMeasure(line);
            if (item.isEmpty() || unit.isEmpty()) continue;
            if (line.getQuantity() <= 0) continue;
            line.setItem(item.get());
            line.setUnit(unit.get());
            line.setContract(target);
            target.addContractLine(line);
        }
    }

    private Optional<SellableItem> resolveSellableItem(ContractLine line) {
        if (line.getItem() == null || line.getItem().getId() == null) {
            return Optional.empty();
        }
        return sellableItemService.findById(line.getItem().getId());
    }

    private Optional<UnitOfMeasure> resolveUnitOfMeasure(ContractLine line) {
        if (line.getUnit() == null || line.getUnit().getId() == null) {
            return Optional.empty();
        }
        return unitOfMeasureService.findById(line.getUnit().getId());
    }
}
