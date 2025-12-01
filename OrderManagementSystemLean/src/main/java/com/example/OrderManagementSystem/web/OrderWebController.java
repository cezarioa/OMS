package com.example.OrderManagementSystem.web;

import com.example.OrderManagementSystem.model.Contract;
import com.example.OrderManagementSystem.model.Customer;
import com.example.OrderManagementSystem.model.Order;
import com.example.OrderManagementSystem.service.OrderService;
import com.example.OrderManagementSystem.service.CustomerService;
import com.example.OrderManagementSystem.service.ContractService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
public class OrderWebController {

    private static final Logger log = LoggerFactory.getLogger(OrderWebController.class);

    private final OrderService orderService;
    private final CustomerService customerService;
    private final ContractService contractService;

    public OrderWebController(OrderService orderService, CustomerService customerService, ContractService contractService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.contractService = contractService;
    }

    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "orders/index";
    }

    @GetMapping("/{id}")
    public String getOrderDetails(@PathVariable("id") Long id, Model model) {
        Order order = orderService.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        model.addAttribute("order", order);
        return "orders/details";
    }

    @GetMapping("/new")
    public String showNewOrderForm(Model model) {
        Order order = new Order();
        // Initialize empty nested objects so the form doesn't crash on th:field="*{customer.id}"
        order.setCustomer(new Customer());
        order.setContract(new Contract());

        model.addAttribute("order", order);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("contracts", contractService.findAll());
        return "orders/form";
    }

    @GetMapping("/{id}/edit")
    public String showEditOrderForm(@PathVariable("id") Long id, Model model) {
        Order order = orderService.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        // Ensure nested objects aren't null for the form bindings
        if (order.getCustomer() == null) order.setCustomer(new Customer());
        if (order.getContract() == null) order.setContract(new Contract());

        model.addAttribute("order", order);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("contracts", contractService.findAll());
        return "orders/form";
    }

    @PostMapping
    public String saveOrder(@Valid @ModelAttribute("order") Order formOrder,
                            BindingResult bindingResult,
                            Model model) {

        // 1. Validate Customer Selection (Required)
        if (formOrder.getCustomer() == null || formOrder.getCustomer().getId() == null) {
            bindingResult.rejectValue("customer.id", "NotNull", "Please select a customer.");
        }

        // 2. Return to form if there are validation errors (e.g. missing name, missing customer)
        if (bindingResult.hasErrors()) {
            model.addAttribute("customers", customerService.findAll());
            model.addAttribute("contracts", contractService.findAll());
            return "orders/form";
        }

        // 3. Load the Real Entities for Associations
        // We must fetch the full Customer object from the DB using the ID from the form.
        Customer realCustomer = customerService.findById(formOrder.getCustomer().getId())
                .orElse(null);

        if (realCustomer == null) {
            bindingResult.rejectValue("customer.id", "NotFound", "Selected customer does not exist.");
            model.addAttribute("customers", customerService.findAll());
            model.addAttribute("contracts", contractService.findAll());
            return "orders/form";
        }

        // Handle Contract (Optional)
        Contract realContract = null;
        if (formOrder.getContract() != null && formOrder.getContract().getId() != null) {
            realContract = contractService.findById(formOrder.getContract().getId()).orElse(null);
        }

        try {
            Order orderToSave;

            if (formOrder.getId() != null) {
                // --- UPDATE SCENARIO ---
                log.info("Updating existing order ID: {}", formOrder.getId());

                // Fetch the existing order from DB to preserve its OrderLines!
                Order existingOrder = orderService.findById(formOrder.getId())
                        .orElseThrow(() -> new RuntimeException("Order not found"));

                // Update editable fields
                existingOrder.setName(formOrder.getName());
                existingOrder.setShippingAddress(formOrder.getShippingAddress());

                // Update relationships
                existingOrder.setCustomer(realCustomer);
                existingOrder.setContract(realContract);

                orderToSave = existingOrder;
            } else {
                // --- CREATE SCENARIO ---
                log.info("Creating new order");
                orderToSave = formOrder;
                orderToSave.setCustomer(realCustomer);
                orderToSave.setContract(realContract);
            }

            Order savedOrder = orderService.save(orderToSave);
            return "redirect:/orders/" + savedOrder.getId();

        } catch (Exception e) {
            log.error("Error saving order", e);
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            model.addAttribute("customers", customerService.findAll());
            model.addAttribute("contracts", contractService.findAll());
            return "orders/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteOrder(@PathVariable("id") Long id) {
        orderService.deleteById(id);
        return "redirect:/orders";
    }
}