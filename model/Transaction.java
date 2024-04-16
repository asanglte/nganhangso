package Asm04.model;

import java.io.Serial;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Transaction implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private TransactionType type;
    private String id;
    private String accountNumber;
    private double amout;
    private String time;
    private boolean status;

    public Transaction(String accountNumber, double amout, boolean status, TransactionType type) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        this.id = String.valueOf(UUID.randomUUID());
        this.accountNumber = accountNumber;
        this.amout = amout;
        this.time = formatter.format(date);
        this.status = status;
        this.type = type;
    }

    public TransactionType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getAmout() {
        return amout;
    }

    public String getTime() {
        return time;
    }

    public boolean getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        Locale locale = Locale.forLanguageTag("vi-VN");
        NumberFormat vi = NumberFormat.getInstance(locale);
        String vnd = "";
        if (getType().getType().equals("WITHDRAW") || getType().getType().equals("TRANSFER")) {
            vnd = "-" + vi.format(getAmout()) + "đ";
        } else {
            vnd = "+" + vi.format(getAmout()) + "đ";
        }
        String status = getStatus() ? "Thành công" : "Thất bại";
        return String.format("%s%-32.16s |%15s\n %11s | %8s |%14s |%24s\n", "MÃ GD: ", getId(), status, getAccountNumber(), getType().getType(), vnd, getTime());
    }
}
