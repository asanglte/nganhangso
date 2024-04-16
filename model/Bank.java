package Asm04.model;

import Asm04.dao.CustomerDao;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class Bank {
    private final String id;

    public Bank() {
        this.id = String.valueOf(UUID.randomUUID());
    }

    public String getId() {
        return id;
    }

    public void addCustomer(Customer newCustomer) {
        if (!isCustomerExisted(newCustomer.getCustomerId())) {
            try {
                List<Customer> customers = getCustomers();
                customers.add(newCustomer);
                CustomerDao.save(customers);
            } catch (Exception e) {
                System.err.println("Thêm khách hàng không thành công: " + e.getMessage());
            }
        }
    }

    public boolean isCustomerExisted(String customerId) {
        return getCustomers().stream().anyMatch(customer -> customer.getCustomerId().equals(customerId));
    }

    public Customer findCustomer(String customerId) {
        if (isCustomerExisted(customerId)) {
            for (Customer customer : getCustomers()) {
                if (customer.getCustomerId().equals(customerId)) {
                    return customer;
                }
            }
        }
        return null;
    }

    public List<Customer> getCustomers() {
        return CustomerDao.list();
    }

    public boolean validateAccount(String accountNumber) {
        return accountNumber.matches("\\d{6}");
    }
}
