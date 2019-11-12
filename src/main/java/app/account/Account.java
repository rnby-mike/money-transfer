package app.account;

import app.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Holds account information.
 */
public class Account {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final long id;
    /**
     * For simplicity BigDecimal is used with scale equal to 2.
     * In real-world application more specialized classes must be used, eg Money from Joda library.
     */
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

    /**
     * Deposit {@code amount} of money to this account.
     * @param amount sum of money to deposit
     * @throws NullPointerException if amount is null
     * @throws IllegalArgumentException if amount less then zero
     */
    public void deposit(BigDecimal amount) {
        Objects.requireNonNull(amount, "deposit amount is null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("deposit amount less than zero");
        }

        // need to synchronize to avoid dirty reads
        lock.writeLock().lock();
        try {
            balance = balance.add(amount).setScale(2, RoundingMode.HALF_UP);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Withdraw {@code amount} of money from this account.
     * @param amount sum of money to withdraw
     * @throws NullPointerException if amount is null
     * @throws IllegalArgumentException if amount less then zero
     * @throws InsufficientBalanceException if account has balance low then amount
     */
    public void withdraw(BigDecimal amount) {
        Objects.requireNonNull(amount, "withdraw amount is null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("withdraw amount less than zero");
        }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Account account = (Account) o;
        return id == account.id && balance.equals(account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance);
    }
}
