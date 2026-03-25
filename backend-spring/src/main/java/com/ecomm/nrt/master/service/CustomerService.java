package com.ecomm.nrt.master.service;

import com.ecomm.nrt.master.dto.CustomerRequest;
import com.ecomm.nrt.master.dto.CustomerResponse;
import com.ecomm.nrt.master.entity.Customer;
import com.ecomm.nrt.master.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    public Page<CustomerResponse> getAll(String search, Pageable pageable) {
        Page<Customer> page = (search != null && !search.isBlank())
                ? customerRepository.searchActive(search, pageable)
                : customerRepository.findByIsActiveTrue(pageable);
        return page.map(c -> modelMapper.map(c, CustomerResponse.class));
    }

    public CustomerResponse getById(Long id) {
        return modelMapper.map(findOrThrow(id), CustomerResponse.class);
    }

    @Transactional
    @SuppressWarnings("null")
    public CustomerResponse create(CustomerRequest request) {
        if (customerRepository.existsByCode(request.getCode()))
            throw new IllegalArgumentException("Customer code already exists: " + request.getCode());
        Customer customer = modelMapper.map(request, Customer.class);
        customer.setIsActive(true);
        return modelMapper.map(customerRepository.save(customer), CustomerResponse.class);
    }

    @Transactional
    @SuppressWarnings("null")
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = findOrThrow(id);
        modelMapper.map(request, customer);
        return modelMapper.map(customerRepository.save(customer), CustomerResponse.class);
    }

    @Transactional
    public void deactivate(Long id) {
        Customer customer = findOrThrow(id);
        customer.setIsActive(false);
        customerRepository.save(customer);
    }

    private Customer findOrThrow(Long id) {
        java.util.Objects.requireNonNull(id, "Customer ID must not be null");
        return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
    }
}
