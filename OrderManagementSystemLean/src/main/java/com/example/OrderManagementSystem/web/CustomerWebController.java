package com.example.OrderManagementSystem.web;

import com.example.OrderManagementSystem.model.Customer;
import com.example.OrderManagementSystem.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customers") // Base URL for all customer web pages
public class CustomerWebController {

    private final CustomerService customerService;

    // We inject the *exact same service* used by the API controller.
    // The business logic is reused.
    public CustomerWebController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * GET /customers
     * Shows the full list of customers[cite: 640].
     * 'Model' is the object we use to pass data from the controller to the template.
     */
    @GetMapping
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerService.findAll());
        // This string "customers/index" tells Spring to render the template at:
        // 'src/main/resources/templates/customers/index.html'
        return "customers/index";
    }

    /**
     * GET /customers/new
     * Shows the blank form to create a new customer[cite: 642].
     */
    @GetMapping("/new")
    public String showNewCustomerForm(Model model) {
        // We add an empty customer object to the model.
        // This object will be "filled" by the form's input fields.
        model.addAttribute("customer", new Customer());
        // This tells Spring to render:
        // 'src/main/resources/templates/customers/form.html'
        return "customers/form";
    }

    /**
     * POST /customers
     * Processes the form submission for creating a new customer[cite: 643].
     *
     * @ModelAttribute binds the form data to the 'customer' object.
     */
    @PostMapping
    public String saveCustomer(@ModelAttribute Customer customer) {
        customerService.save(customer);
        // "redirect:" is a best practice (Post-Redirect-Get pattern).
        // It prevents duplicate form submissions if the user refreshes.
        // This will redirect the browser to GET /customers.
        return "redirect:/customers";
    }

    /**
     * POST /customers/{id}/delete
     * Deletes a customer[cite: 643].
     *
     * @PathVariable reads the 'id' from the URL.
     */
    @PostMapping("/{id}/delete")
    public String deleteCustomer(@PathVariable Long id) {
        customerService.deleteById(id);
        return "redirect:/customers";
    }
}