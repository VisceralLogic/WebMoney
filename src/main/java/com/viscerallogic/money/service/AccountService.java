package com.viscerallogic.money.service;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jakarta.inject.Singleton;
import java.util.*;
import java.net.URI;

import com.viscerallogic.money.*;
import com.viscerallogic.money.bean.*;

@Path("/account")
@Singleton
public class AccountService{
	private Hashtable<String, Account> accounts = new Hashtable<String, Account>();

	@GET
	@Produces("application/json")
	@Path("/list")
	public Set<String> getAccounts(){
		return accounts.keySet();
	}

	@POST
	@Path("/create")
	@Consumes("text/plain")
	public void addAccount(String name){
		Account account = new Account();
		account.setName(name);
		
		if( accounts.containsKey(name) )
			throw new WebApplicationException("Account already exists", Response.Status.CONFLICT);

		accounts.put(name, account);	
	}

	@GET
	@Path("/{account}/transactions")
	@Produces("application/json")
	public Vector<Transaction> getTransactions(@PathParam("account") String name){
		if( !accounts.containsKey(name) )
			throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);

		return accounts.get(name).getTransactions();
	}

	@POST
	@Path("/{account}/add")
	@Consumes("application/json")
	public void addTransaction(@PathParam("account") String name, Transaction t){
		if( !accounts.containsKey(name) )
			throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);

		accounts.get(name).addTransaction(t);		
	}

	@POST
	@Path("/{account}/remove")
	@Consumes("application/json")
	public void removeTransaction(@PathParam("account") String name, Transaction t){
		if( !accounts.containsKey(name) )
			throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);

		boolean result = accounts.get(name).removeTransaction(t);

		if( !result )
			throw new WebApplicationException("Transaction not found", Response.Status.NOT_FOUND);
	}
}