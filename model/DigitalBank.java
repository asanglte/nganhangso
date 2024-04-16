package Asm04.model;

import Asm04.dao.AccountDao;
import Asm04.dao.CustomerDao;
import Asm04.service.TextFileService;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import static Asm04.service.TextFileService.IS_DEBUG;

public class DigitalBank extends Bank implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final String TEST_PATH = "vn.funix.fx21645.java/src/Asm04/store/customer.txt";

    public void showCustomer() {
        if (getCustomers().isEmpty()) {
            System.out.println("Không có khách hàng nào trong dánh sách");
        } else {
            for (Customer customer : getCustomers()) {
                customer.displayInformation();
                System.out.println();
            }
        }
    }

    public void addCustomerFromtxtFile(String fileName) {
        List<Customer> customers = CustomerDao.list();
        List<Customer> newCustomers = TextFileService.readFiletxt(IS_DEBUG ? TEST_PATH : fileName);

        for (Customer newCustomer : newCustomers) {
            if (!isCustomerExisted(customers, newCustomer)) {
                customers.add(newCustomer);
                System.out.println("Thêm thành công khách hàng " + newCustomer.getCustomerId() + " vào danh sách");
            } else {
                System.out.println("Khách hàng " + newCustomer.getCustomerId() + " đã có trong danh sách");
            }
        }

        try {
            CustomerDao.save(customers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addAccount(Account newAccount) {
        if (newAccount.getCustomer() != null) {
            if (getAccountByAccountNumber(newAccount.getAccountNumber()) == null) {
                newAccount.getCustomer().addAccount(newAccount);
            }
        }
    }

    public void addSavingsAccount(Scanner scanner, String customerId) {
        Customer customer = getCustomerById(CustomerDao.list(), customerId);

        if (customer == null) {
            System.out.println("Khách hàng không có trong danh sách");
        } else {
            SavingsAccount newSavingsAccount = new SavingsAccount(customerId);
            newSavingsAccount.input(scanner);
        }
    }


    public Customer getCustomerById(List<Customer> customerList, String customerId) {
        return customerList.stream().filter(customer -> customer.getCustomerId().equals(customerId)).findAny().orElse(null);
    }

    public boolean isAccountExisted(List<Account> accountList, Account newAccount) {
        return accountList.stream().anyMatch(account -> account.getAccountNumber().equals(newAccount.getAccountNumber()));
    }

    public boolean isCustomerExisted(List<Customer> customerList, Customer newCustomer) {
        return customerList.stream().anyMatch(customer -> customer.getCustomerId().equals(newCustomer.getCustomerId()));
    }

    public Account getAccountByAccountNumber(String accountNumber) {
        for (Customer customer : getCustomers()) {
            if (customer.getAccountByAccountNumber(customer.getAccounts(), accountNumber) != null) {
                return customer.getAccountByAccountNumber(AccountDao.list(), accountNumber);
            }
        }
        return null;
    }

    private void processCustomerAction(String customerId, Consumer<Customer> action) {
        // Tìm khách hàng thông qua customerId
        Customer customer = getCustomerById(getCustomers(), customerId);

        if (customer == null) {
            System.out.println("Khách hàng không có trong danh sách");
        } else {
            // Hiển thị thông tin khách hàng
            customer.displayInformation();

            // Thực hiện hành động được truyền vào qua đối tượng Consumer
            action.accept(customer);
        }
    }

    // Phương thức withdraw sử dụng processCustomerAction để thực hiện withdraw của khách hàng
    public void withdraw(Scanner scanner, String customerId) {
        // Gọi processCustomerAction với hành động là customer.withdraw(scanner)
        processCustomerAction(customerId, customer -> customer.withdraw(scanner));
    }

    // Phương thức transfer sử dụng processCustomerAction để thực hiện transfer của khách hàng
    public void transfer(Scanner scanner, String customerId) {
        // Gọi processCustomerAction với hành động là customer.transfer(scanner)
        processCustomerAction(customerId, customer -> customer.transfer(scanner));
    }

    // Phương thức Transactions sử dụng processCustomerAction để hiển thị thông tin giao dịch của khách hàng
    public void Transactions(String customerId) {
        // Gọi processCustomerAction với hành động là Customer::displayTransactionInformation
        processCustomerAction(customerId, Customer::displayTransactionInformation);
    }

}
