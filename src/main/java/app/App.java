package app;

import app.account.AccountController;
import app.account.AccountDao;
import app.account.AccountService;
import app.exception.ErrorResponse;
import app.exception.InsufficientBalanceException;
import app.exception.NoSuchAccountException;
import app.transfer.TransferController;
import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.core.validation.JavalinValidation;

import java.math.BigDecimal;

import static io.javalin.apibuilder.ApiBuilder.*;

public class App {

    public static AccountService accountService = new AccountService(new AccountDao());

    void start(int port) {
        JavalinValidation.register(BigDecimal.class, BigDecimal::new);

        var app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.logIfServerNotStarted = true;
            config.defaultContentType = "application/json";
            config.registerPlugin(new RouteOverviewPlugin("/info"));
        });

        app.routes(() -> {
            path("/accounts", () -> {
                post(AccountController.createAccount);
                path(":id", () -> {
                    get(AccountController.findAccount);
                    delete(AccountController.deleteAccount);
                    post("deposit", AccountController.deposit);
                    post("withdrawal", AccountController.withdraw);
                });
            });
            post("/transfers", TransferController.transfer);
        });


        app.exception(NoSuchAccountException.class, (ex, ctx) ->
                ctx.status(404).json(new ErrorResponse(404, ex.getMessage())));

        app.exception(InsufficientBalanceException.class, (ex, ctx) ->
                ctx.status(400).json(new ErrorResponse(400, "Insufficient balance")));

        app.start(port);

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }
}