package com.viscerallogic.money.bean;

import java.util.*;

public class Account {
	private String name = "<New Account>";
	private Vector<Transaction> transactions = new Vector<Transaction>();

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setTransactions(Vector<Transaction> transactions){
		this.transactions = transactions;
	}

	public Vector<Transaction> getTransactions(){
		return transactions;
	}

	public void addTransaction(Transaction t){
		int i = 0;
		while( i < transactions.size() ){
			if( transactions.get(i).getDate().compareTo(t.getDate()) >= 0 )
				break;
			i++;
		}
		transactions.insertElementAt(t, i);
	}

	public boolean removeTransaction(Transaction t){
		for( Transaction trans : transactions ){
			if( trans.equals(t) ){
				transactions.removeElement(trans);
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString(){
		StringBuilder account = new StringBuilder("{" + name + ":");
		for( Enumeration<Transaction> e = transactions.elements(); e.hasMoreElements(); ){
			Transaction t = e.nextElement();
			account.append(t);
			if( e.hasMoreElements() ){
				account.append(",\n");
			}
		}
		account.append("}");
		return account.toString();
	}
}