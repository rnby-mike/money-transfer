package app.account;

import app.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Account {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final long id;
    private BigDecimal balance;

    public Account(long id, BigDecimal initialBalance) {
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException();
        }
        this.id = id;
        this.balance = initialBalance.setScale(2, RoundingMode.HALF_UP);
    }

    public long getId() {
        return id;
    }

    public BigDecimal getBalance() {
        lock.readLock().lock();
        try {
            return balance;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void deposit(BigDecimal amount) {
        // need to synchronize to avoid dirty reads
        lock.writeLock().lock();
        try {
            balance = balance.add(amount).setScale(2, RoundingMode.HALF_UP);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void withdraw(BigDecimal amount) {
        lock.writeLock().lock();
        try {
            if (balance.compareTo(amount) < 0) {
                throw new InsufficientBalanceException();
            }
            balance = balance.subtract(amount).setScale(2, RoundingMode.HALF_UP);
        } finally {
            lock.writeLock().unlock();
        }
    }

    ReadWriteLock getLock() {
        return lock;
    }
}
