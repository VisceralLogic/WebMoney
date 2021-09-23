package com.viscerallogic.money;

import java.util.*;
import java.time.LocalDate;

import com.viscerallogic.money.bean.Transaction;

public class TransactionUtilities{
    public static int valueOfTransactions(Set<Transaction> transactions){
		int cents = 0;
	
		for( Transaction t : transactions ){
		    cents += t.getCents();
		}

		return cents;
    }

    public static int valueOfTransactionsAtDate(Set<Transaction> transactions, LocalDate date){
		int cents = 0;

		for( Transaction t : transactions ){
		    if( t.getDate().compareTo(date) <= 0 )
			cents += t.getCents();
		}

		return cents;
    }

    public String htmlTable(List<Transaction> transactions){
		StringBuilder builder = new StringBuilder("<table border=\"1\">\n");
		builder.append("<tr><th>Date<th>Account<th>Ref<th>Payee<th>Memo<th>Category<th>Reconciled<th>Amount\n");

		for( Transaction t : transactions ){
		    boolean negative = t.getCents() < 0;
		    int multiplier = negative ? -1 : 1;
		}

		builder.append("</table>\n");
		return builder.toString();
    }
}
