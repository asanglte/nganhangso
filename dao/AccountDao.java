package Asm04.dao;

import Asm04.model.Account;
import Asm04.service.BinaryFileService;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Asm04.service.BinaryFileService.IS_DEBUG;

public class AccountDao implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final String FILE_PATH = "vn.funix.fx21645.java/src/Asm04/store/account.dat";
    private static String TEST_PATH = "vn.funix.fx21645.java/src/Asm04/file/testaccount.dat";

    public static void save(List<Account> accounts) throws IOException {
        BinaryFileService.writeFile(IS_DEBUG ? TEST_PATH : FILE_PATH, accounts);
    }

    public static List<Account> list() {
        return BinaryFileService.readFile(IS_DEBUG ? TEST_PATH : FILE_PATH);
    }

    public static void update(Account editAccount) {
        // Lấy danh sách tài khoản hiện có
        List<Account> accounts = list();

        // Nếu danh sách rỗng, thêm tài khoản vào
        if (accounts.isEmpty()) {
            accounts.add(editAccount);
        } else {
            // Kiểm tra xem tài khoản cần cập nhật đã tồn tại chưa
            boolean hasExist = accounts.stream().anyMatch(account -> account.getAccountNumber().equals(editAccount.getAccountNumber()));

            // Nếu chưa tồn tại, thêm tài khoản vào danh sách
            if (!hasExist) {
                accounts.add(editAccount);
            } else {
                // Tạo một ThreadPool để xử lý song song việc cập nhật tài khoản
                ExecutorService executorService = Executors.newFixedThreadPool(4);

                // Duyệt qua danh sách tài khoản
                for (int i = 0; i < accounts.size(); i++) {
                    Account account = accounts.get(i);

                    // Thực hiện việc cập nhật tài khoản trong một Thread riêng biệt
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            // Nếu tài khoản cần cập nhật được tìm thấy, thì sẽ thay thế nó trong danh sách
                            if (account.getAccountNumber().equals(editAccount.getAccountNumber())) {
                                accounts.set(accounts.indexOf(account), editAccount);
                            }
                        }
                    });
                }
                // Đóng ThreadPool
                executorService.shutdown();
            }
        }

        // Lưu danh sách tài khoản sau khi cập nhật
        try {
            save(accounts);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
