package Asm04.model;

import Asm04.exception.CustomerIdNotValidException;

import java.io.Serial;
import java.io.Serializable;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
    private String customerId;

    public User() {

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) throws CustomerIdNotValidException {
        if (!customerId.matches("\\d+") || customerId.length() != 12) {
            throw new CustomerIdNotValidException("Số CCCD " + customerId + " không đúng định dạng");
        } else {
            this.customerId = customerId;
        }
    }
}
