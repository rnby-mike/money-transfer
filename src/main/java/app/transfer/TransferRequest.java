package app.transfer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public final class TransferRequest {
    private final long senderAccountId;
    private final long receiverAccountId;
    private final BigDecimal amount;

    @JsonCreator
    public TransferRequest(@JsonProperty("senderAccountId") long senderAccountId,
                           @JsonProperty("receiverAccountId") long receiverAccountId,
                           @JsonProperty("amount") BigDecimal amount) {
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.amount = amount;
    }

    long getSenderAccountId() {
        return senderAccountId;
    }

    long getReceiverAccountId() {
        return receiverAccountId;
    }

    BigDecimal getAmount() {
        return amount;
    }
}
