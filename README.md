## How to run
```
./gradlew run
```
By default app starts on port 8080. You can specify  MONEY_TRANSFER_SERVER_PORT env variable to run on different port.
```
MONEY_TRANSFER_SERVER_PORT=8081 ./gradlew run
```
 
## Endpoints
* `POST /accounts` - creates account in the system.  
Request body:
```json
{
	"initialBalance": "float"
}
```
* `GET /accounts/{id}` - retrieves account information 
* `DELETE /accounts/{id}` - deletes account
* `POST /accounts/{id}/deposit?amount={amount}` - deposits some amount of money to the account
* `POST /accounts/{id}/withdrawal?amount={amount}` - withdraws some amount of money from the account
* `POST /transfers` - transfers money from one account to another  
Request body:
```json
{
	"senderAccountId": "integer",
	"receiverAccountId": "integer",
	"amount": "float"
}
```

You can see it in action in [test](src/test/java/app/MoneyTransferAppIT.java)
