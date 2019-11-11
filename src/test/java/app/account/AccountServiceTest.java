package app.account;

import app.exception.InsufficientBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(new AccountDao());
    }

    @Test
    void shouldCreateAccountWithInitialBalance() {
        var initialBalance = BigDecimal.TEN;

        var account = accountService.createAccount(initialBalance);

        assertNotNull(account);
        assertEquals(0, initialBalance.compareTo(account.getBalance()));
    }

    @Test
    void shouldNotCreateAccountWithNegativeInitialBalance() {
        var initialBalance = BigDecimal.ONE.negate();

        assertThrows(InsufficientBalanceException.class, () -> accountService.createAccount(initialBalance));
    }

    @Test
    void shouldCorrectlyDeposit() {
        var account = accountService.createAccount(BigDecimal.TEN);

        accountService.deposit(account.getId(), BigDecimal.ONE);

        assertEquals(new BigDecimal("11.00"), accountService.findAccountById(account.getId()).getBalance());
    }

    @Test
    void shouldWithdrawEnoughAmount() {
        var account = accountService.createAccount(BigDecimal.TEN);

        accountService.withdraw(account.getId(), BigDecimal.ONE);

        assertEquals(new BigDecimal("9.00"), accountService.findAccountById(account.getId()).getBalance());
    }

    @Test
    void shouldNotWithdrawWhenInsufficientAmount() {
        var account = accountService.createAccount(BigDecimal.ONE);

        assertThrows(InsufficientBalanceException.class,
                () -> accountService.withdraw(account.getId(), BigDecimal.TEN));
    }

    @Test
    void shouldCorrectlyTransferMoney() {
        var senderInitialBalance = new BigDecimal("100.50");
        var transferAmount = new BigDecimal("5.50");
        var receiverInitialBalance = new BigDecimal("10.00");

        var senderAccount = accountService.createAccount(senderInitialBalance);
        var receiverAccount = accountService.createAccount(receiverInitialBalance);

        accountService.transfer(senderAccount.getId(), receiverAccount.getId(), transferAmount);

        assertEquals(senderInitialBalance.subtract(transferAmount),
                accountService.findAccountById(senderAccount.getId()).getBalance());

        assertEquals(receiverInitialBalance.add(transferAmount),
                accountService.findAccountById(receiverAccount.getId()).getBalance());
    }

    @Test
    void shouldNotAllowTransferMoneyToSameAccount() {
        assertThrows(IllegalArgumentException.class,
                () -> accountService.transfer(1L, 1L, BigDecimal.ONE));
    }

    @Test
    void shouldNotTransferWhenInsufficientAmount() {
        var senderInitialBalance = new BigDecimal("100.50");
        var transferAmount = new BigDecimal("105.50");
        var receiverInitialBalance = new BigDecimal("10.00");

        var senderAccount = accountService.createAccount(senderInitialBalance);
        var receiverAccount = accountService.createAccount(receiverInitialBalance);

        assertThrows(InsufficientBalanceException.class,
                () -> accountService.transfer(senderAccount.getId(), receiverAccount.getId(), transferAmount));
    }
}