package account;

import java.math.BigDecimal;

public class Account {
    private final String id;
    private final BigDecimal balance;

    public Account(String id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }
}
