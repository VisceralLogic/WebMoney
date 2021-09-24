package com.viscerallogic.money.service;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jakarta.inject.Singleton;
import jakarta.servlet.ServletContext;
import jakarta.json.bind.*;
import jakarta.json.bind.spi.*;

import java.util.*;
import java.net.URI;
import java.io.*;

import com.viscerallogic.money.*;
import com.viscerallogic.money.bean.*;

@Singleton
@Path("/")
public class AccountService{
	private Hashtable<String, Account> accounts = new Hashtable<String, Account>();

	private @Context ServletContext context;
	private @Context SecurityContext security;

	Jsonb jsonb = JsonbProvider.provider().create().build();

	public void saveAccounts(){
		//String json = jsonb.toJson(accounts);
		String path = context.getInitParameter("directory") + security.getUserPrincipal().getName() + ".json";
		try{
			jsonb.toJson(accounts, new FileWriter(path));	
		} catch( Exception e ){
			throw new WebApplicationException("Error saving file", Response.Status.INTERNAL_SERVER_ERROR);
		}
		
	}

	@GET
	@Produces("application/json")
	@Path("/list")
	public Set<String> getAccounts(){
		System.out.println(context.getRealPath("/"));
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

		saveAccounts();
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
			throw new WebApplicationException("Account not found: " + name, Response.Status.NOT_FOUND);

		//check if this is a transfer between accounts
		if( t.getCategory().matches("^\\[.+\\]$") ){
			String category = t.getCategory();
			String other = category.substring(1,category.length());
			if( !accounts.containsKey(other) )
				throw new WebApplicationException("Account not found: " + other, Response.Status.NOT_FOUND);

			Transaction t2 = t.clone();
			t2.setCategory("[" + name + "]");
			t2.setCents(-t.getCents());

			accounts.get(other).addTransaction(t2);
		}

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