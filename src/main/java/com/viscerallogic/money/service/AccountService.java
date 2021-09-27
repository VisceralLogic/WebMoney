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
	// Hashtable<User name, Hashtable<Account name, Account object> >
	private Hashtable<String, Hashtable<String, Account> > accounts = new Hashtable<String, Hashtable<String, Account> >();

	private ServletContext context;
	private String directoryName;
	private @Context SecurityContext security;

	Jsonb jsonb = JsonbProvider.provider().create().build();

	@Context
	public void setServletContext(ServletContext context){
		this.context = context;
		directoryName = context.getInitParameter("directory");
		loadAccounts();
		if( !accounts.containsKey(getUserName()) ){
			accounts.put(getUserName(), new Hashtable<String, Account>());
		}
	}

	public void saveAccounts(){
		String userName = getUserName();
		String path = directoryName + userName + ".json";
		try{
			jsonb.toJson(accounts.get(userName), new FileWriter(path));	
		} catch( Exception e ){
			throw new WebApplicationException("Error saving file", Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	public void loadAccounts(){
		File directory = new File(directoryName);
		String[] fileList = directory.list(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.matches("^.*\\.json$");
		    }
		});

		try{
			for( String filename : fileList ){
				String userName = filename.substring(0, filename.length()-5);
				Hashtable<String, Account> user = jsonb.fromJson(new FileReader(directoryName + filename), new Hashtable<String, Account>(){}.getClass().getGenericSuperclass());
				accounts.put(userName, user);
			}
		} catch( FileNotFoundException e ){
			throw new WebApplicationException("Error loading accounts", Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	protected String getUserName(){
		return security.getUserPrincipal().getName();
	}

	protected Hashtable<String, Account> getUserAccounts(){
		return  accounts.get(getUserName());
	}

	@GET
	@Produces("application/json")
	@Path("/list")
	public Set<String> getAccounts(){
		return getUserAccounts().keySet();
	}

	@POST
	@Path("/create")
	@Consumes("text/plain")
	public void addAccount(String name){
		Account account = new Account();
		account.setName(name);
		
		if( getUserAccounts().containsKey(name) )
			throw new WebApplicationException("Account already exists", Response.Status.CONFLICT);

		getUserAccounts().put(name, account);

		saveAccounts();
	}

	@GET
	@Path("/{account}/transactions")
	@Produces("application/json")
	public Vector<Transaction> getTransactions(@PathParam("account") String name){
		if( !getUserAccounts().containsKey(name) )
			throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);

		return getUserAccounts().get(name).getTransactions();
	}

	@POST
	@Path("/{account}/add")
	@Consumes("application/json")
	public void addTransaction(@PathParam("account") String name, Transaction t){
		Hashtable<String, Account> user = getUserAccounts();
		if( !user.containsKey(name) )
			throw new WebApplicationException("Account not found: " + name, Response.Status.NOT_FOUND);

		//check if this is a transfer between accounts
		if( t.getCategory().matches("^\\[.+\\]$") ){
			String category = t.getCategory();
			String other = category.substring(1,category.length()-1);

			if( !user.containsKey(other) )
				throw new WebApplicationException("Account not found: " + other, Response.Status.NOT_FOUND);

			Transaction t2 = t.clone();
			t2.setCategory("[" + name + "]");
			t2.setCents(-t.getCents());

			user.get(other).addTransaction(t2);
		}

		user.get(name).addTransaction(t);

		saveAccounts();		
	}

	@POST
	@Path("/{account}/remove")
	@Consumes("application/json")
	public void removeTransaction(@PathParam("account") String name, Transaction t){
		Hashtable<String, Account> user = getUserAccounts();
		if( !user.containsKey(name) )
			throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);

		//check if this is a transfer between accounts
		if( t.getCategory().matches("^\\[.+\\]$") ){
			String category = t.getCategory();
			String other = category.substring(1,category.length()-1);

			if( !user.containsKey(other) )
				throw new WebApplicationException("Account not found: " + other, Response.Status.NOT_FOUND);

			Transaction t2 = t.clone();
			t2.setCategory("[" + name + "]");
			t2.setCents(-t.getCents());

			boolean result = user.get(other).removeTransaction(t2);
			if( !result )
				throw new WebApplicationException("Transaction not found in other account", Response.Status.NOT_FOUND);
		}

		boolean result = user.get(name).removeTransaction(t);
		if( !result )
			throw new WebApplicationException("Transaction not found in account", Response.Status.NOT_FOUND);

		saveAccounts();
	}
}