package com.example.OrderManagementSystem.web;

import com.example.OrderManagementSystem.model.Contract;
import com.example.OrderManagementSystem.model.Customer;
import com.example.OrderManagementSystem.model.Order;
import com.example.OrderManagementSystem.service.OrderService;
import com.example.OrderManagementSystem.service.CustomerService;
import com.example.OrderManagementSystem.service.ContractService;
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
@RequestMapping("/orders")
public class OrderWebController {

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
        if (order.getCustomer() == null) {
            order.setCustomer(new Customer());
        }
        if (order.getContract() == null) {
            order.setContract(new Contract());
        }

        model.addAttribute("order", order);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("contracts", contractService.findAll());
        return "orders/form";
    }

    @PostMapping
    public String saveOrder(@Valid @ModelAttribute("order") Order formOrder,
                            BindingResult bindingResult,
                            Model model) {

        // 1. Validate Associations
        if (formOrder.getCustomer() == null || formOrder.getCustomer().getId() == null) {
            bindingResult.rejectValue("customer.id", "NotNull", "Please select a customer.");
        }

        // 2. Validate existence of referenced entities
        if (!bindingResult.hasErrors()) {
            customerService.findById(formOrder.getCustomer().getId())
                    .ifPresentOrElse(formOrder::setCustomer,
                            () -> bindingResult.rejectValue("customer.id", "NotFound", "Selected customer does not exist."));

            if (formOrder.getContract() != null && formOrder.getContract().getId() != null) {
                contractService.findById(formOrder.getContract().getId())
                        .ifPresentOrElse(formOrder::setContract,
                                () -> bindingResult.rejectValue("contract.id", "NotFound", "Selected contract does not exist."));
            } else {
                formOrder.setContract(null);
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("order", formOrder);
            model.addAttribute("customers", customerService.findAll());
            model.addAttribute("contracts", contractService.findAll());
            return "orders/form";
        }

        Order orderToSave;

        // 3. CRITICAL FIX: Handle Updates vs Inserts
        if (formOrder.getId() != null) {
            // UPDATE: Fetch existing from DB to preserve OrderLines
            Order existingOrder = orderService.findById(formOrder.getId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Update only the editable fields
            existingOrder.setName(formOrder.getName());
            existingOrder.setShippingAddress(formOrder.getShippingAddress());
            existingOrder.setCustomer(formOrder.getCustomer());
            existingOrder.setContract(formOrder.getContract());

            orderToSave = existingOrder;
        } else {
            // CREATE: Use the new object directly
            orderToSave = formOrder;
        }

        Order savedOrder = orderService.save(orderToSave);
        return "redirect:/orders/" + savedOrder.getId();
    }

    @PostMapping("/{id}/delete")
    public String deleteOrder(@PathVariable("id") Long id) {
        orderService.deleteById(id);
        return "redirect:/orders";
    }
}