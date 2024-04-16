package Asm04.model;

import Asm04.IReport;
import Asm04.ITransfer;
import Asm04.Withdraw;
import Asm04.dao.AccountDao;
import Asm04.dao.TransactionDao;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SavingsAccount extends Account implements IReport, Withdraw, ITransfer, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public final double SAVINGS_ACCOUNT_MAX_WITHDRAW = 5000000;

    public SavingsAccount(String customerId, String accountNumber, double balance) {
        super(customerId, accountNumber, balance);
    }

    public SavingsAccount(String customerId) {
        super(customerId);
    }

    //Thực hiện rút tiền từ tài khoản.
    @Override
    public boolean withdraw(double amount) {
        // Xác định loại giao dịch là WITHDRAW
        TransactionType type = TransactionType.WITHDRAW;

        // Lấy danh sách các giao dịch từ lớp TransactionDao
        List<Transaction> transactions = TransactionDao.list();

        // Kiểm tra xem rút tiền có hợp lệ không
        if (!isAccepted(amount)) {
            // Xử lý giao dịch thất bại và trả về false
            handleFailedTransaction(transactions, amount, type);
            return false;
        }

        // Xử lý giao dịch rút tiền thành công và trả về true
        handleSuccessfulWithdrawal(transactions, amount, type);
        return true;
    }

    //Thực hiện giao dịch chuyển tiền đến tài khoản khác.
    public boolean transfer(Account receiveAccount, double amount) {
        // Xác định loại giao dịch chuyển tiền và loại giao dịch nạp tiền
        TransactionType transferType = TransactionType.TRANSFER;
        TransactionType depositType = TransactionType.DEPOSIT;

        // Lấy danh sách các giao dịch từ lớp TransactionDao
        List<Transaction> transactions = TransactionDao.list();

        // Kiểm tra xem giao dịch chuyển tiền có hợp lệ không
        if (!isAccepted(amount)) {
            // Xử lý giao dịch thất bại và trả về false
            handleFailedTransaction(transactions, amount, transferType);
            return false;
        }

        // Xử lý giao dịch chuyển tiền thành công và cập nhật giao dịch nạp tiền tương ứng
        handleSuccessfulTransfer(transactions, receiveAccount, amount, transferType, depositType);
        return true;
    }

    private void handleFailedTransaction(List<Transaction> transactions, double amount, TransactionType type) {
        System.out.println("Giao dịch không thành công");
        transactions.add(new Transaction(getAccountNumber(), amount, false, type));

        try {
            TransactionDao.save(transactions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleSuccessfulWithdrawal(List<Transaction> transactions, double amount, TransactionType type) {
        System.out.println("Rút tiền thành công, biên lai giao dịch: ");
        setBalance(getBalance() - amount);
        log(amount, type, "");
        transactions.add(creatTransaction(amount, true, type));

        try {
            TransactionDao.save(transactions);
            AccountDao.update(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleSuccessfulTransfer(List<Transaction> transactions, Account receiveAccount, double amount, TransactionType transferType, TransactionType depositType) {
        System.out.println("Chuyển tiền thành công, biên lai giao dịch: ");
        // Giảm số dư của tài khoản chuyển tièn trừ đi số tiền chuyển
        setBalance(getBalance() - amount);

        // Tăng số dư của tài khoản nhận tiền cộng với số tiền đã chuyển
        receiveAccount.setBalance(receiveAccount.getBalance() + amount);
        // Ghi log cho giao dịch chuyển tiền với thông tin số tiền, loại giao dịch và số tài khoản nhận
        log(amount, transferType, receiveAccount.getAccountNumber());

        // Tạo và thêm giao dịch chuyển tiền vào danh sách giao dịch
        transactions.add(creatTransaction(amount, true, transferType));
        // Tạo và thêm giao dịch nạp tiền (để tương ứng với tiền chuyển đi) vào danh sách giao dịch của tài khoản nhận
        transactions.add(receiveAccount.creatTransaction(amount, true, depositType));

        try {
            TransactionDao.save(transactions);
            AccountDao.update(this);
            AccountDao.update(receiveAccount);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isAccepted(double amount) {
        Locale locale = Locale.forLanguageTag("vi-VN");
        NumberFormat vi = NumberFormat.getInstance(locale);

        boolean isPremium = isPremium();
        double balanceAfterWithdraw = getBalance() - amount;
        boolean isAmountMultipleOf10000 = amount % 10000 == 0;

        if (amount < 50000) {
            System.out.println("Không thể rút/chuyển nhỏ hơn 50.000đ");
        } else if (balanceAfterWithdraw < 50000) {
            System.out.println("Số dư không đủ để thực hiện giao dịch");
            System.out.println("Số dư của bạn: " + vi.format(getBalance()) + "đ");
        } else if (!isAmountMultipleOf10000) {
            System.out.println("Số tiền rút/chuyển phải là bội số của 10.000đ");
        } else if (isPremium && balanceAfterWithdraw >= 50000) {
            return true;
        } else if (!isPremium && balanceAfterWithdraw >= 50000 && amount <= SAVINGS_ACCOUNT_MAX_WITHDRAW) {
            return true;
        } else if (!isPremium && amount > SAVINGS_ACCOUNT_MAX_WITHDRAW) {
            System.out.println("Tài khoản của bạn chỉ có thể rút tối đa 5.000.000đ/lượt");
        }
        return false;
    }

    @Override
    public String toString() {
        Locale locale = Locale.forLanguageTag("vi-VN");
        NumberFormat vi = NumberFormat.getInstance(locale);
        String vnd = vi.format(this.getBalance()) + "đ";
        return String.format("%11s |%12s |%33s", this.getAccountNumber(), "SAVINGS", vnd);
    }

    @Override
    public void log(double amount, TransactionType type, String receiveAccount) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        Locale locale = Locale.forLanguageTag("vi-VN");
        NumberFormat vi = NumberFormat.getInstance(locale);
        String soTien = vi.format(amount) + "đ";
        String soDu = vi.format(getBalance()) + "đ";
        System.out.printf("%31s\n", "BIEN LAI GIAO DỊCH SAVINGS");
        System.out.printf("Ngay G/D:" + "%31s\n", formatter.format(date));
        System.out.printf("ATM ID:" + "%33s\n", "DIGITAL-BANK-ATM 2022");
        System.out.printf("SO TK:" + "%34s\n", getAccountNumber());
        if (!receiveAccount.equals("")) {
            System.out.printf("SO TK NHAN TIEN:" + "%24s\n", receiveAccount);
        }
        switch (type.getType()) {
            case "TRANSFER" -> System.out.printf("SO TIEN CHUYEN:" + "%25s\n", soTien);
            case "WITHDRAW" -> System.out.printf("SO TIEN RUT:" + "%28s\n", soTien);
            case "DEPOSIT" -> System.out.printf("SO TIEN NAP:" + "%28s\n", soTien);
        }
        System.out.printf("SO DU TK:" + "%31s\n", soDu);
        System.out.printf("PHI + VAT:" + "%30s\n", "0đ");
    }
}
