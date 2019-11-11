package app;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.Matchers.*;

class MoneyTransferAppIT {
    @BeforeAll
    static void start() {
        int port = 12345;
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        new App().start(port);
    }

    @Test
    void shouldCreateAccount() {
        given().
                body("{\"initialBalance\": 100.00}").
        when().
                post("/accounts").
        then().
                statusCode(201).
                body("balance", is(equalTo(new BigDecimal("100.00"))));

    }

    @Test
    void shouldNotCreateAccountWithNegativeInitialBalance() {
        given().
                body("{\"initialBalance\": -100.00}").
        when().
                post("/accounts").
        then().
                statusCode(400).
                body("message", is(notNullValue()));
    }

    @Test
    void shouldReturnAccountById() {
        var initialBalance = new BigDecimal("10.00");
        int accountId = createAccount(initialBalance);

        given().
                pathParam("id", accountId).
        when().
                get("/accounts/{id}").
        then().
                statusCode(200).
                body("id", is(equalTo(accountId))).
                body("balance", is(equalTo(initialBalance)));
    }

    @Test
    void shouldNotFoundAccountWhenAccountDoesNotExist() {
        given().
                pathParam("id", -1).
        when().
                get("/accounts/{id}").
        then().
                statusCode(404).
                body("message", is(notNullValue()));
    }

    @Test
    void shouldCorrectlyDepositMoney() {
        int accountId = createAccount(new BigDecimal("100.00"));

        given().
                pathParam("id", accountId).
                queryParam("amount", 10.10).
        when().
                post("/accounts/{id}/deposit").
        then().
                statusCode(200).
                body("id", is(equalTo(accountId))).
                body("balance", is(equalTo(new BigDecimal("110.10"))));

    }

    @Test
    void shouldCorrectlyWithdrawMoney() {
        int accountId = createAccount(new BigDecimal("100.00"));

        given().
                pathParam("id", accountId).
                queryParam("amount", 10.10).
        when().
                post("/accounts/{id}/withdrawal").
        then().
                statusCode(200).
                body("id", is(equalTo(accountId))).
                body("balance", is(equalTo(new BigDecimal("89.90"))));

    }

    @Test
    void shoudlCorrectlyTransferMoney() {
        int senderAccountId = createAccount(new BigDecimal("100.00"));
        int receiverAccountId = createAccount(new BigDecimal("50.00"));

        given().
                body("{\n" +
                        "\"senderAccountId\": " + senderAccountId +"," +
                        "\"receiverAccountId\": " + receiverAccountId + "," +
                        "\"amount\": 25.00" +
                      "}").
        when().
                post("/transfers").
        then().
                statusCode(200);


        var expectedBalance = new BigDecimal("75.00");
        given().
                pathParam("id", senderAccountId).
        when().
                get("/accounts/{id}").
        then().
                statusCode(200).
                body("id", is(equalTo(senderAccountId))).
                body("balance", is(equalTo(expectedBalance)));

        given().
                pathParam("id", receiverAccountId).
        when().
                get("/accounts/{id}").
                then().
        statusCode(200).
                body("id", is(equalTo(receiverAccountId))).
                body("balance", is(equalTo(expectedBalance)));


    }

    private static int createAccount(BigDecimal initialBalance) {
        return
        given().
                body("{\"initialBalance\": " + initialBalance + "}").
        when().
                post("/accounts").
        then().
                statusCode(201).
                extract().
                body().
                jsonPath().
                getInt("id");
    }
}