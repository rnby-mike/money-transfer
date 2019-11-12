package app.account;

import app.exception.NoSuchAccountException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store of accounts information.
 * For the sake of simplicity {@code ConcurrentHashMap} is used as backing store
 * instead of some kind of in-memory dbms, eg. H2 or HSQLDB
 */
public class AccountDao {
    private static final Map<Long, Account> ACCOUNTS = new ConcurrentHashMap<>();

    void addAccount(Account account) {
        ACCOUNTS.put(account.getId(), account);
    }

    Account findById(long id) {
        var account = ACCOUNTS.get(id);
        if (account == null) {
            throw new NoSuchAccountException(id);
        }
        return account;
    }

    void deleteById(long id) {
        var deletedAccount = ACCOUNTS.remove(id);
        if (deletedAccount == null) {
            throw new NoSuchAccountException(id);
        }
    }

}
