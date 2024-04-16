package Asm04;


import Asm04.model.Account;

public interface ITransfer{
    boolean transfer(Account recieveAccount, double amount);
}