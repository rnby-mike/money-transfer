package app.account;

import app.exception.NoSuchAccountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountDaoTest {

    private AccountDao accountDao;

    @BeforeEach
    void setUp() {
        accountDao = new AccountDao();
    }

    @Test
    void shouldFindExistingAccount() {
        var account = new Account(1L, BigDecimal.TEN);

        accountDao.addAccount(account);

        assertEquals(account, accountDao.findById(1L));
    }

    @Test
    void shouldThrowExceptionWhenNoAccountFound() {
        assertThrows(NoSuchAccountException.class, () -> accountDao.findById(1L));
    }

    @Test
    void shouldDeleteExistingAccountById() {
        var account = new Account(1L, BigDecimal.TEN);
        accountDao.addAccount(account);

        accountDao.deleteById(1L);

        assertThrows(NoSuchAccountException.class, () -> accountDao.findById(1L));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNotExistingAccount() {
        assertThrows(NoSuchAccountException.class, () -> accountDao.deleteById(1L));
    }
}