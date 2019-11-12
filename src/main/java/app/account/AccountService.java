package app.account;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

/**
 * Service for managing accounts in the system.
 * In real app it should be separate interface for such purpose.
 */
public class AccountService {
    private static final AtomicLong ACCOUNT_IDS_SEQUENCE = new AtomicLong(0);
    private final AccountDao accountDao;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    /**
     * Finds account by id.
     * @param id account id
     * @throws app.exception.NoSuchAccountException in case there is no account in store
     * @return account stores in the system with provided id
     */
    Account findAccountById(long id) {
        return accountDao.findById(id);
    }

    /**
     * Create account with initial balance.
     * @param balance initial balance
     * @return newly created account
     */
    Account createAccount(BigDecimal balance) {
        var account = new Account(ACCOUNT_IDS_SEQUENCE.incrementAndGet(), balance);
        accountDao.addAccount(account);
        return account;
    }

    /**
     * Deletes account by id
     * @param id account id
     * @throws app.exception.NoSuchAccountException in case there is no account in store
     */
    void deleteAccountById(long id) {
        accountDao.deleteById(id);
    }

    /**
     * Deposit {@code amount} of money to account
     * @param accountId id of account to deposit to
     * @param amount money
     * @return account with updated balance
     */
    Account deposit(long accountId, BigDecimal amount) {
        var account = accountDao.findById(accountId);
        account.deposit(amount);
        return account;

    }

    /**
     * Withdraw {@code amount} of money from account
     * @param accountId id of account to withdraw from
     * @param amount money
     * @return account with updated balance
     */
    Account withdraw(long accountId, BigDecimal amount) {
        var account = accountDao.findById(accountId);
        account.withdraw(amount);
        return account;
    }

    /**
     * Transfers money from one account to another
     * @param senderAccountId account id to withdraw from
     * @param receiverAccountId account id to deposit to
     * @param amount money
     */
    public void transfer(long senderAccountId, long receiverAccountId, BigDecimal amount) {

        if (senderAccountId == receiverAccountId) {
            throw new IllegalArgumentException("sender is receiver are the same");
        }

        var senderAccount = accountDao.findById(senderAccountId);
        var receiverAccount = accountDao.findById(receiverAccountId);

        // determine the order of acquiring locks to avoid deadlocks
        Lock senderLock;
        Lock receiverLock;
        if (senderAccountId < receiverAccountId) {
            senderLock = senderAccount.getLock().writeLock();
            receiverLock = receiverAccount.getLock().writeLock();
        } else {
            senderLock = receiverAccount.getLock().writeLock();
            receiverLock = senderAccount.getLock().writeLock();
        }

        senderLock.lock();
        receiverLock.lock();

        try {
            senderAccount.withdraw(amount);
            receiverAccount.deposit(amount);
        } finally {
            senderLock.unlock();
            receiverLock.unlock();
        }
    }
}
