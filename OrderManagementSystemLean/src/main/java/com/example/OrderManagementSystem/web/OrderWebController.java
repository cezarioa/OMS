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

    /**
     * GET /orders/{id}
     * Shows the details page for an existing order. (NEW)
     */
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

    /**
     * GET /orders/{id}/edit
     * Shows the populated form to edit an existing order. (NEW)
     */
    @GetMapping("/{id}/edit")
    public String showEditOrderForm(@PathVariable("id") Long id, Model model) {
        Order order = orderService.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
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
    public String saveOrder(@Valid @ModelAttribute("order") Order order,
                            BindingResult bindingResult,
                            Model model) {
        ensureAssociations(order);

        if (order.getCustomer() == null || order.getCustomer().getId() == null) {
            bindingResult.rejectValue("customer.id", "NotNull", "Please select a customer.");
        }

        if (order.getContract() != null && order.getContract().getId() == null) {
            order.setContract(null);
        }

        if (!bindingResult.hasErrors()) {
            customerService.findById(order.getCustomer().getId())
                    .ifPresentOrElse(order::setCustomer,
                            () -> bindingResult.rejectValue("customer.id", "NotFound", "Selected customer does not exist."));

            if (order.getContract() != null && order.getContract().getId() != null) {
                contractService.findById(order.getContract().getId())
                        .ifPresentOrElse(order::setContract,
                                () -> bindingResult.rejectValue("contract.id", "NotFound", "Selected contract does not exist."));
            } else {
                order.setContract(null);
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("order", order);
            model.addAttribute("customers", customerService.findAll());
            model.addAttribute("contracts", contractService.findAll());
            return "orders/form";
        }

        Order savedOrder = orderService.save(order);
        return "redirect:/orders/" + savedOrder.getId();
    }

    private void ensureAssociations(Order order) {
        if (order.getCustomer() == null) {
            order.setCustomer(new Customer());
        }
        if (order.getContract() == null) {
            order.setContract(new Contract());
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteOrder(@PathVariable("id") Long id) {
        orderService.deleteById(id);
        return "redirect:/orders";
    }
}
