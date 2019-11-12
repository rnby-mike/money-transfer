package app.account;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class AccountServiceStressIT {

    private final BigDecimal initialBalance = new BigDecimal("100000.00");
    private final BigDecimal amountToTransferFromFirstAccount = new BigDecimal("0.50");
    private final BigDecimal amountToTransferFromSecondAccount = new BigDecimal("1.00");

    private final AccountService accountService = new AccountService(new AccountDao());
    private final Account firstAccount = accountService.createAccount(initialBalance);
    private final Account secondAccount = accountService.createAccount(initialBalance);

    @Test
    void shouldCorrectlyTransferMoneyInMultithreadedEnv() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        int numOfTransfers = 100_000;
        CountDownLatch finishedSignal = new CountDownLatch(numOfTransfers * 2);
        Collection<Callable<Void>> tasks = new ArrayList<>(numOfTransfers * 2);
        for (int i = 0; i < numOfTransfers; i++) {
            tasks.add(runTransferMultipleTimes(firstAccount.getId(), secondAccount.getId(),
                    amountToTransferFromFirstAccount, finishedSignal));
            tasks.add(runTransferMultipleTimes(secondAccount.getId(), firstAccount.getId(),
                    amountToTransferFromSecondAccount, finishedSignal));
        }
        executor.invokeAll(tasks);
        finishedSignal.await();

        var totalTransferedFromFirstAccount =
                amountToTransferFromFirstAccount.multiply(BigDecimal.valueOf(numOfTransfers));
        var totalTransferedFromSecondAccount =
                amountToTransferFromSecondAccount.multiply(BigDecimal.valueOf(numOfTransfers));

        assertThat(firstAccount.getBalance(),is(
                equalTo(initialBalance.subtract(totalTransferedFromFirstAccount).add(totalTransferedFromSecondAccount)
        )));

        assertThat(secondAccount.getBalance(), is(
                equalTo(initialBalance.subtract(totalTransferedFromSecondAccount).add(totalTransferedFromFirstAccount)
        )));

        assertThat(firstAccount.getBalance().add(secondAccount.getBalance()), is(
                equalTo(initialBalance.add(initialBalance))));
    }

    private Callable<Void> runTransferMultipleTimes(long senderAccountId,
                                                    long receiverAccountId,
                                                    BigDecimal amountToTransfer,
                                                    CountDownLatch finishedSignal) {
        return () -> {
            accountService.transfer(senderAccountId, receiverAccountId, amountToTransfer);
            finishedSignal.countDown();
            return null;
        };
    }
}