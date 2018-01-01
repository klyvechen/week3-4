package org.test.model;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class MyDate extends Date{
	/**
	 * 
	 */
	private SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd");
	private static final long serialVersionUID = 1L;

	public MyDate(long date) {
		super(date);
	}
	
	@Override
	public String toString(){
		return sdFormat.format(super.getTime());
	}
}
