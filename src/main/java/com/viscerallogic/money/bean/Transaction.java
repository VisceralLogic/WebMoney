package com.viscerallogic.money.bean;

import jakarta.json.bind.annotation.*;
import java.time.LocalDate;

public class Transaction implements Cloneable {
	@JsonbDateFormat("yyyy/MM/dd")
    private LocalDate date = LocalDate.now();
    private String ref = "";
    private String payee = "";
    private String category = "";
    private String memo = "";
    private boolean cleared = false;
    private int cents = 0;

    public Transaction(){}

	@Override
	public Transaction clone(){
		try {
			return (Transaction)super.clone();
		} catch( CloneNotSupportedException e ){
			return null;
		}
	}

    public void setDate(LocalDate date){
		this.date = date;
    }

    public LocalDate getDate(){
		return date;
    }

    public void setRef(String ref){
		this.ref = ref;
    }

    public String getRef(){
		return ref;
    }

    public void setPayee(String payee){
		this.payee = payee;
    }

    public String getPayee(){
		return payee;
    }

    public void setCategory(String category){
		this.category = category;
    }

    public String getCategory(){
		return category;
    }

    public void setMemo(String memo){
		this.memo = memo;
    }

    public String getMemo(){
		return memo;
    }

    public void setCleared(boolean cleared){
		this.cleared = cleared;
    }

    public boolean isCleared(){
		return cleared;
    }

    public void setCents(int cents){
		this.cents = cents;
    }

    public int getCents(){
		return cents;
    }

    @Override
    public boolean equals(Object o){
    	if( o == this )
    		return true;

    	if( !(o instanceof Transaction) )
    		return false;

    	Transaction t = (Transaction)o;

    	if( t.date.compareTo(date) != 0 ||
	    		t.ref.compareTo(ref) != 0 ||
	    		t.payee.compareTo(payee) != 0 ||
	    		t.category.compareTo(category) != 0 ||
	    		t.memo.compareTo(memo) != 0 ||
	    		t.cleared != cleared ||
	    		t.cents != cents ){
    		return false;
    	}

    	return true;
    }
}
