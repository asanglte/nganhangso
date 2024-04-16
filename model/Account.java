package Asm04.model;

import Asm04.dao.AccountDao;
import Asm04.dao.CustomerDao;
import Asm04.dao.TransactionDao;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Account implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Locale locale = Locale.forLanguageTag("vi-Vn");
    private NumberFormat vi = NumberFormat.getInstance(locale);
    private String customerId;
    private String accountNumber;
    private double balance;

    public Account(String customerId, String accountNumber, double balance) {
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public Account(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<Transaction> getTransactions() {
        return TransactionDao.list().stream().filter(transaction -> transaction.getAccountNumber().equals(accountNumber)).collect(Collectors.toList());
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public boolean isPremium() {
        return this.balance >= 10000000;
    }

    public String toString() {
        return String.format("%s |%52s", accountNumber, vi.format(getBalance()) + "đ");
    }

    public Customer getCustomer() {
        for (Customer customer : CustomerDao.list()) {
            if (customer.getCustomerId().equals(getCustomerId())) {
                return customer;
            }
        }
        return null;
    }

    public void displayTransactionList() {
        for (Transaction transaction : getTransactions()) {
            System.out.println(transaction.toString());
        }
    }

    public Transaction creatTransaction(double amount, boolean status, TransactionType type) {
        // Tạo và trả về một đối tượng giao dịch mới
        return new Transaction(getAccountNumber(), amount, status, type);
    }

    public void input(Scanner scanner) {
        while (true) {
            System.out.println("\nVui lòng nhập Số tài khoản của bạn!");
            System.out.print("Nhập 6 chữ số: ");
            accountNumber = scanner.nextLine();
            if (!new Bank().validateAccount(accountNumber)) {
                if (accountNumber.equalsIgnoreCase("No")) {
                    return;
                }
                System.out.println("\nLưu ý: Số tài khoản vừa nhập chưa đúng, nhập lại hoặc No để thoát");
            } else {
                List<Account> accounts = AccountDao.list();
                boolean accountExists = accounts.stream().anyMatch(account -> account.getAccountNumber().equals(accountNumber));
                if (accountExists) {
                    System.out.println("\nLưu ý: Số tài khoản vừa nhập đã tồn tại, nhập lại hoăc No để thoát");
                } else {
                    while (balance < 50000 || balance % 10000 != 0) {
                        System.out.println("\nVui lòng nhập số tiền, tối thiểu 50.000đ");
                        System.out.print("vnđ: ");
                        try {
                            balance = scanner.nextDouble();
                            scanner.nextLine();
                        } catch (Exception e) {
                        }
                        if (balance == -1) {
                            return;
                        }
                        if (balance < 50000 || balance % 10000 != 0) {
                            System.out.print("\nLưu ý: ");
                            if (balance % 10000 != 0) {
                                System.out.println("Số dư phải là bội số của 10.000đ, nhập lại hoặc -1 để thoát");
                            }
                            if (balance < 50000 && balance % 10000 != 0) {
                                System.out.println("Số dư không được nhỏ hơn 50.000đ, nhập lại hoặc -1 để thoát");
                            }
                        } else {
                            accounts.add(new SavingsAccount(getCustomerId(), getAccountNumber(), getBalance()));
                            try {
                                AccountDao.save(accounts);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            System.out.println("\nThêm thành công: "+ getCustomer().getName()+ "||" + getCustomerId());
                            System.out.println("Loại TK: SAVINGS");
                            System.out.println("STK: " + getAccountNumber());
                            System.out.println("Số dư: " + vi.format(getBalance()) + "đ");
                            // Lấy danh sách các giao dịch từ lớp TransactionDao
                            List<Transaction> transactions = TransactionDao.list();

                            // Tạo một giao dịch mới với thông tin về số tiền, trạng thái thành công và loại giao dịch là "DEPOSIT"
                            Transaction newTransaction = creatTransaction(getBalance(), true, TransactionType.DEPOSIT);

                            // Thêm giao dịch mới vào danh sách các giao dịch
                            transactions.add(newTransaction);

                            try {
                                // Lưu danh sách giao dịch mới đã được cập nhật vào lớp TransactionDao
                                TransactionDao.save(transactions);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

}
