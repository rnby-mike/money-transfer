package app.account;

import app.exception.InsufficientBalanceException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountTest {

    @Test
    void shouldCreateAccountWithPositiveInitialBalance() {
        var initialBalance = BigDecimal.TEN;

        var account = new Account(1L, initialBalance);

        assertEquals(0, initialBalance.compareTo(account.getBalance()));
        assertEquals(2, account.getBalance().scale());
        assertEquals(BigInteger.valueOf(1000L), account.getBalance().unscaledValue());
    }

    @Test
    void shouldNotCreateAccountWithNegativeInitialBalance() {
        var initialBalance = BigDecimal.ONE.negate();

        assertThrows(InsufficientBalanceException.class, () -> new Account(1L, initialBalance));
    }

    @Test
    void shouldDepositPositiveAmount() {
        var account = new Account(1L, BigDecimal.TEN);

        account.deposit(BigDecimal.valueOf(20.221));

        assertEquals(new BigDecimal("30.22"), account.getBalance());
    }

    @Test
    void shouldNotDepositNegativeAmount() {
        var account = new Account(1L, BigDecimal.TEN);

        assertThrows(IllegalArgumentException.class, () -> account.deposit(BigDecimal.valueOf(-0.221)));
    }

    @Test
    void shouldWithdrawEnoughAmount() {
        var account = new Account(1L, BigDecimal.TEN);

        account.withdraw(BigDecimal.valueOf(0.221));

        assertEquals(new BigDecimal("9.78"), account.getBalance());
    }

    @Test
    void shouldNotWithdrawWhenInsufficientBalance() {
        var account = new Account(1L, BigDecimal.TEN);

        assertThrows(InsufficientBalanceException.class, () -> account.withdraw(BigDecimal.valueOf(10.221)));
    }
}