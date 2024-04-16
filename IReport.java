package Asm04;

import Asm04.model.TransactionType;

public interface IReport {
    void log(double amount, TransactionType type, String receiveAccount);
}