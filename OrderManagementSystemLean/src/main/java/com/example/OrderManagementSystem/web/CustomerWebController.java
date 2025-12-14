package com.example.OrderManagementSystem.web;

import com.example.OrderManagementSystem.model.Customer;
import com.example.OrderManagementSystem.repository.CustomerSpecifications;
import com.example.OrderManagementSystem.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.BindingResult;

import java.util.Set;

@Controller
@RequestMapping("/customers") // Base URL for all customer web pages
public class CustomerWebController {

    private static final Set<String> SORTABLE_FIELDS = Set.of("id", "name", "currency", "email");
    private final CustomerService customerService;

    // We inject the *exact same service* used by the API controller.
    // The business logic is reused.
    public CustomerWebController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * GET /customers
     * Shows the full list of customers.
     * 'Model' is the object we use to pass data from the controller to the template.
     */
    @GetMapping
    public String listCustomers(@RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "currency", required = false) String currency,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {

        Sort sort = resolveSort(sortBy, sortDir, SORTABLE_FIELDS, "id");
        Specification<Customer> spec = CustomerSpecifications.withFilters(name, currency, email);
        model.addAttribute("customers", customerService.searchCustomers(spec, sort));

        model.addAttribute("name", name);
        model.addAttribute("currency", currency);
        model.addAttribute("email", email);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortableFields", SORTABLE_FIELDS);
        // This string "customers/index" tells Spring to render the template at:
        // 'src/main/resources/templates/customers/index.html'
        return "customers/index";
    }

    /**
     * GET /customers/{id}
     * Shows the details page for an existing customer. (NEW)
     */
    @GetMapping("/{id}")
    public String getCustomerDetails(@PathVariable("id") Long id, Model model) {
        Customer customer = customerService.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
        model.addAttribute("customer", customer);
        return "customers/details";
    }

    /**
     * GET /customers/new
     * Shows the blank form to create a new customer.
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
     * GET /customers/{id}/edit
     * Shows the populated form to edit an existing customer. (NEW)
     */
    @GetMapping("/{id}/edit")
    public String showEditCustomerForm(@PathVariable("id") Long id, Model model) {
        Customer customer = customerService.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
        model.addAttribute("customer", customer);
        // Reuses the form template
        return "customers/form";
    }

    /**
     * POST /customers
     * Processes the form submission for creating or updating a customer.
     *
     * @ModelAttribute binds the form data to the 'customer' object.
     */
    @PostMapping
    public String saveCustomer(@Valid @ModelAttribute("customer") Customer customer,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("customer", customer);
            return "customers/form";
        }

        Customer savedCustomer = customerService.save(customer);
        // Redirect to the details page of the saved/updated customer
        return "redirect:/customers/" + savedCustomer.getId();
    }

    /**
     * POST /customers/{id}/delete
     * Deletes a customer.
     *
     * @PathVariable reads the 'id' from the URL.
     */
    @PostMapping("/{id}/delete")
    public String deleteCustomer(@PathVariable("id") Long id) {
        customerService.deleteById(id);
        return "redirect:/customers";
    }

    private Sort resolveSort(String sortBy, String sortDir, Set<String> allowed, String fallback) {
        if (sortBy == null || !allowed.contains(sortBy)) {
            sortBy = fallback;
        }
        Sort sort = Sort.by(sortBy);
        if ("desc".equalsIgnoreCase(sortDir)) {
            return sort.descending();
        }
        return sort.ascending();
    }
}
