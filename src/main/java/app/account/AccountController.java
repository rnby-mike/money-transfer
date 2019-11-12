package app.account;

import io.javalin.http.Handler;

import java.math.BigDecimal;

import static app.App.accountService;

/**
 * Holds a bunch of handler to process accounts related requests.
 */
public class AccountController {

    public static Handler createAccount = ctx -> {
        var balance = ctx.bodyAsClass(CreateAccountRequest.class).getInitialBalance();
        var account = accountService.createAccount(balance);
        ctx.json(account);
        ctx.status(201);
    };

    public static Handler findAccount = ctx -> {
        var accountId = ctx.pathParam("id", long.class).get();
        var account = accountService.findAccountById(accountId);
        ctx.json(account);
    };

    public static Handler deleteAccount = ctx -> {
        var accountId = ctx.pathParam("id", long.class).get();
        accountService.deleteAccountById(accountId);
    };

    public static Handler deposit = ctx -> {
        var accountId = ctx.pathParam("id", long.class).get();
        var amount = ctx.queryParam("amount", BigDecimal.class).get();
        var updatedAccount = accountService.deposit(accountId, amount);
        ctx.json(updatedAccount);
    };

    public static Handler withdraw = ctx -> {
        var accountId = ctx.pathParam("id", long.class).get();
        var amount = ctx.queryParam("amount", BigDecimal.class).get();
        var updatedAccount = accountService.withdraw(accountId, amount);
        ctx.json(updatedAccount);
    };


}
