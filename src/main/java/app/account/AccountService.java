package app.account;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

public class AccountService {
    private static final AtomicLong ACCOUNT_IDS_SEQUENCE = new AtomicLong(0);
    private final AccountDao accountDao;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    Account findAccountById(long id) {
        return accountDao.findById(id);
    }

    Account createAccount(BigDecimal balance) {
        var account = new Account(ACCOUNT_IDS_SEQUENCE.incrementAndGet(), balance);
        accountDao.addAccount(account);
        return account;
    }

    void deleteAccountById(long id) {
        accountDao.deleteById(id);
    }

    Account deposit(long accountId, BigDecimal amount) {
        var account = accountDao.findById(accountId);
        account.deposit(amount);
        return account;

    }

    Account withdraw(long accountId, BigDecimal amount) {
        var account = accountDao.findById(accountId);
        account.withdraw(amount);
        return account;
    }

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
