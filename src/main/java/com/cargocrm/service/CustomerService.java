package com.cargocrm.service;

import com.cargocrm.dto.CustomerCreateRequest;
import com.cargocrm.dto.CustomerResponse;
import com.cargocrm.dto.CustomerUpdateRequest;
import com.cargocrm.dto.PageResponse;
import com.cargocrm.entity.Customer;
import com.cargocrm.exception.ConflictException;
import com.cargocrm.exception.CustomerNotFoundException;
import com.cargocrm.pagination.PaginationUtil;
import com.cargocrm.repository.CustomerRepository;
import com.cargocrm.repository.CustomerSpecification;
import com.cargocrm.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public CustomerService(CustomerRepository customerRepository,
                           OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    private Customer findCustomerOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id: " + id + "not found"));
    }

    @Transactional
    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setCompanyName(request.companyName());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setAddress(request.address());
        customer.setNotes(request.notes());

        Customer saved = customerRepository.saveAndFlush(customer);
        return CustomerResponse.from(saved);
    }

    public PageResponse<CustomerResponse> getCustomers(int page,
                                                       int limit,
                                                       String sortBy,
                                                       String sortDir,
                                                       String search) {
        List<String> permissionList = List.of("createdAt", "updatedAt", "name");

        Pageable pageable = PaginationUtil.createPageable(
                page,
                limit,
                sortBy,
                sortDir,
                permissionList,
                "createdAt");

        String trimmedSearch = (search != null) ? search.trim() : null;
        Page<Customer> customerPage;
        if (trimmedSearch == null || trimmedSearch.trim().isEmpty()) {
            customerPage = customerRepository.findAll(pageable);
        } else {
            customerPage = customerRepository.findAll(
                    CustomerSpecification.search(trimmedSearch), pageable);
        }
        Page<CustomerResponse> responsePage = customerPage.map(CustomerResponse::from);

        return PageResponse.from(responsePage);
    }

    public CustomerResponse getCustomerById(Long id) {
        Customer customer = findCustomerOrThrow(id);

        return CustomerResponse.from(customer);
    }

    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerUpdateRequest update) {
        Customer customer = findCustomerOrThrow(id);

        customer.setName(update.name());
        customer.setCompanyName(update.companyName());
        customer.setEmail(update.email());
        customer.setPhone(update.phone());
        customer.setAddress(update.address());
        customer.setNotes(update.notes());
        customerRepository.saveAndFlush(customer);

        return CustomerResponse.from(customer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = findCustomerOrThrow(id);
        if (orderRepository.existsByCustomerId(id)) {
            throw new ConflictException("Customer has orders");
        }
        customerRepository.delete(customer);
    }
}
