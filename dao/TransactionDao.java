package Asm04.dao;

import Asm04.model.Transaction;
import Asm04.service.BinaryFileService;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import static Asm04.service.BinaryFileService.IS_DEBUG;
public class TransactionDao implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final String FILE_PATH = "vn.funix.fx21645.java/src/Asm04/store/transaction.dat";
    private static final String TEST_PATH = "vn.funix.fx21645.java/src/Asm04/file/testtransaction.dat";

    public static void save(List<Transaction> customers) throws IOException {
        BinaryFileService.writeFile(IS_DEBUG ? TEST_PATH : FILE_PATH, customers);
    }

    public static List<Transaction> list() {
        return BinaryFileService.readFile(IS_DEBUG ? TEST_PATH : FILE_PATH);
    }
}
