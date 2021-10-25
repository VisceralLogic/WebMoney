# WebMoney

This project provides a JAX-RS interface for a personal finance web service

To set this up, your JavaEE server needs an authentication user in the "file" realm.
You also need to create a directory with read-write permission for the JavaEE process, and update the context-param "directory" in the "WEB-INF/web.xml" configuration file with its path, including the closing "/".

This application has been tested on Glassfish 6.

The accounts overview page allows creation and access of accounts:  
![accounts overview](https://www.viscerallogic.com/img/WebMoneyIndex.png)

Transactions can be created and deleted in the account access page:  
![accounts overview](https://www.viscerallogic.com/img/WebMoneyAccount1.png)

Transfers are automatically adjusted from the linked account using the "category" field:  
![accounts overview](https://www.viscerallogic.com/img/WebMoneyAccount2.png)
