package com.jan.rm.dao.ds;

public class TelActionPair extends ActionPair {
	
	private String number;

	public TelActionPair(String title, String number) {
		super(title, 0);
		
		this.number = number;
	}

	public void setNumber(String number){
		this.number = number;
	}
	
	public String getNumber(){
		return number;
	}
}
