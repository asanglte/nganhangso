package Asm04.model;

import java.io.Serial;
import java.io.Serializable;

public class TransactionType implements Serializable {
    public static final TransactionType DEPOSIT = new TransactionType("DEPOSIT");
    public static final TransactionType WITHDRAW = new TransactionType("WITHDRAW");
    public static final TransactionType TRANSFER = new TransactionType("TRANSFER");

    @Serial
    private static final long serialVersionUID = 1L;
    private final String type;

    private TransactionType(String type) {
        this.type = type;
    }

    public static TransactionType getDeposit() {
        return DEPOSIT;
    }

    public static TransactionType getWithdraw() {
        return WITHDRAW;
    }

    public static TransactionType getTransfer() {
        return TRANSFER;
    }

    public String getType() {
        return type;
    }
}

