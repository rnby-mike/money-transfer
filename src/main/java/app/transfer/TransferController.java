package app.transfer;

import io.javalin.http.Handler;

import static app.App.accountService;

public class TransferController {

    public static Handler transfer = ctx -> {
        var transferRequest = ctx.bodyAsClass(TransferRequest.class);
        accountService.transfer(transferRequest.getSenderAccountId(),
                transferRequest.getReceiverAccountId(), transferRequest.getAmount());
        ctx.status(200);
    };
}
