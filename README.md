# MoneyTransferApplication

A Java REST API for money transfers between accounts

Description :
Application built using jersey API and having H2 in memory database. Log4j is used for logging. while starting the application, H2 in memory database initialized with some sample data.

To Run : 
Starting point : Application.java   

APIs : 
Ping Api : http://localhost:8080/api/ping

Transaction : http://localhost:8080/transaction
Req : 
	{
		"fromAccount" : 1,
		"toAccount" : 2,
		"amount" : 100,
		"currencyType" : EURO/DOLLOR/INR,
		"status" : INITIATED,
		"createdAt" : 1567621800000,
		"updatedAt" : 1567621800000,
	}

All User Get : http://localhost:8080/user/all-users

Get User by Name : http://localhost:8080/user/{name}

Delete User : http://localhost:8080/user/{user-id}

Create User : http://localhost:8080/user/user/create

User Creation Req : 
	{
		"name": "MSD",
		"mail": "msd@gmail.com",
		"createdAt": 1567621800000,
		"updatedAt": 1567621800000
	}

Update User by Id : http://localhost:8080/user/1

User Updation Req : 
	{
		"name": "MSD",
		"mail": "msd@gmail.com",
		"createdAt": 1567621800000,
		"updatedAt": 1567621800000
	}

All Account Get : http://localhost:8080/account/all

Get Account by Id : http://localhost:8080/account/{account-id}

Get Account balance by Id : http://localhost:8080/account/{account-id}/balance

Create Account : http://localhost:8080/account/create

Create Account Req : 
{
		"type": "SAVINGS",
		"userId": 7,
		"balance": 100,
		"currencyType": "EURO",
		"status": "OPEN",
		"createdAt": 1567621800000,
		"updatedAt": 1567621800000
}

Deposit : http://localhost:8080/account/{accountId}/deposit/{amount}

Withdraw  : http://localhost:8080/account/{accountId}/withdraw/{amount}

Delete : http://localhost:8080/account/{accountId}
