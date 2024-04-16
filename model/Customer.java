package Asm04.model;

import Asm04.dao.AccountDao;
import Asm04.exception.CustomerIdNotValidException;

import java.io.Serial;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;


public class Customer extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public Customer(String name, String customerId) throws CustomerIdNotValidException {
        super.setName(name);
        super.setCustomerId(customerId);
    }

    public List<Account> getAccounts() {
        return AccountDao.list().stream().filter(account -> account.getCustomerId().equals(getCustomerId())).collect(Collectors.toList());
    }

    public boolean isPremium(List<Account> accounts) {
        for (Account account : accounts) {
            if (account.getBalance() >= 10000000) {
                return true;
            }
        }
        return false;
    }

    public void addAccount(Account newAccount) {
        if (!isAccountExisted(newAccount.getAccountNumber())) {
            AccountDao.update(newAccount);
        }
    }

    public boolean isAccountExisted(String accountNumber) {
        return getAccounts().stream().anyMatch(account -> account.getAccountNumber().equals(accountNumber));
    }

    public double getBalence() {
        double balence = 0;
        for (Account account : getAccounts()) {
            balence += account.getBalance();
        }
        return balence;
    }

    public void displayInformation() {
        Locale locale =Locale.forLanguageTag("vi-VN");
        NumberFormat vi = NumberFormat.getInstance(locale);
        String vnd = vi.format(getBalence()) + "đ";
        String str = isPremium(getAccounts()) ? "Premium" : "Normal";
        System.out.printf("%12s |%20s |%8s |%16s\n", getCustomerId(), getName(), str, vnd);
        int n = 0;
        for (Account account : getAccounts()) {
            System.out.println(++n + " " + account.toString());
        }
    }

    // hiển thị thông tin lịch sử giao dịch của khách hàng
    public void displayTransactionInformation() {
        getAccounts().forEach(Account::displayTransactionList);
    }


    public Account getAccountByAccountNumber(List<Account> accounts, String accountNumber) {
        // Sử dụng Stream để duyệt qua danh sách tài khoản
        // Sử dụng filter để lọc các tài khoản có số tài khoản trùng với accountNumber
        // Sử dụng findAny để trả về một tài khoản, hoặc trả về null nếu không có
        return accounts.stream()
                .filter(account -> account.getAccountNumber().equals(accountNumber))
                .findAny()
                .orElse(null);
    }

    public void withdraw(Scanner scanner) {
        if (!getAccounts().isEmpty()) {
            Account account;
            String accountNumber;
            double amount = 0;
            do {
                System.out.println("\nVui lòng nhập STK của bạn !");
                System.out.print("STK: ");
                accountNumber = scanner.nextLine();
                account = getAccountByAccountNumber(getAccounts(), accountNumber);
                if (account == null) {
                    if (accountNumber.equalsIgnoreCase("No")) {
                        return;
                    }
                    System.out.println("\nLưu ý: STK chưa đúng, Vui lòng nhập lại hoặc No để thoát");
                }
            } while (account == null);
            while (amount < 50000 & amount != -1 || amount % 10000 != 0) {
                System.out.print("\nNhập số tiền rút cần rút! vnd: ");
                try {
                    amount = scanner.nextDouble();
                    scanner.nextLine();
                } catch (Exception e) {
                    scanner.nextLine();
                }
                if (amount == -1) {
                    return;
                } else {
                    if (amount < 50000) {
                        System.out.println("Không thể rút nhỏ hơn 50.000đ");
                    } else if (amount % 10000 != 0) {
                        System.out.println("Số tiền chuyển phải là bội số của 10.000");
                    }
                }
            }
            if (account instanceof SavingsAccount) {
                ((SavingsAccount) account).withdraw(amount);
            }
        }
    }

    public void transfer(Scanner scanner) {
        if (!getAccounts().isEmpty()) {
            Account account;
            Account receiveAccount;
            String accountNumber;
            String receiveAccountNumber;
            double amount = 0;
            do {
                System.out.println("\nVui lòng nhập số tài khoản chuyển tiền !");
                System.out.print("STK: ");
                accountNumber = scanner.nextLine();
                account = getAccountByAccountNumber(getAccounts(), accountNumber);
                if (account == null) {
                    if (accountNumber.equalsIgnoreCase("No")) {
                        return;
                    }
                    System.out.println("\nLưu ý: Số tài khoản chưa đúng, nhập lại hoặc No để thoát");
                }
            } while (account == null);
            do {
                System.out.println("\nVui lòng nhập số tài khoản nhận tiền !");
                System.out.print("STK: ");
                receiveAccountNumber = scanner.nextLine();
                receiveAccount = getAccountByAccountNumber(AccountDao.list(), receiveAccountNumber);
                if (receiveAccount == null) {
                    if (accountNumber.equalsIgnoreCase("No")) {
                        return;
                    }
                    System.out.println("\nLưu ý: Số tài khoản chưa đúng, nhập lại hoặc No để thoát");
                } else {
                    System.out.println("\nQuý khách đang thực hiện chuyển tiền đến:\nChủ tài khoản: " + receiveAccount.getCustomer().getName() + "\nSTK: " + receiveAccountNumber);
                }
            } while (receiveAccount == null);
            while (amount < 50000 && amount != -1 || amount % 10000 != 0) {
                System.out.println("\nVui lòng nhập số tiền cần chuyển!");
                System.out.print("vnđ: ");
                try {
                    amount = scanner.nextDouble();
                    scanner.nextLine();
                } catch (Exception e) {
                    scanner.nextLine();
                }
                if (amount == -1) {
                    return;
                } else {
                    System.out.print("\nLưu ý: ");
                    if (amount % 10000 != 0) {
                        System.out.println("Số tiền chuyển phải là bội số của 10.000đ");
                    } else if (amount < 50000 && amount % 10000 == 0) {
                        System.out.println("Không thể chuyển nhỏ hơn 50.000đ");
                    }
                }
            }
            if (account instanceof SavingsAccount) {
                String input = "";
                while (!input.equalsIgnoreCase("Y") && !input.equalsIgnoreCase("N")) {
                    Locale locale =Locale.forLanguageTag("vi-VN");
                    NumberFormat vi = NumberFormat.getInstance(locale);
                    System.out.print("Xác nhận chuyển " + vi.format(amount) + "đ "
                            + "\nTừ tài khoản " + account.getAccountNumber()
                            + " ==> tài khoản " + receiveAccount.getAccountNumber()
                            + " \n(Y/N): ");

                    input = scanner.nextLine();
                    if (input.equalsIgnoreCase("Y")) {
                        ((SavingsAccount) account).transfer(receiveAccount, amount);
                        return;
                    } else if (input.equalsIgnoreCase("N")) {
                        System.out.println("\nHủy chuyển tiên");
                        return;
                    }
                }
            }
        }
    }
}
