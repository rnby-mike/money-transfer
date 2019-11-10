package app.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public final class CreateAccountRequest {
    private final BigDecimal initialBalance;

    @JsonCreator
    public CreateAccountRequest(@JsonProperty("initialBalance") BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }
}
