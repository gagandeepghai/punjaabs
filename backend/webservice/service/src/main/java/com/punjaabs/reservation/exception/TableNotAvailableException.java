package com.punjaabs.reservation.exception;

public class TableNotAvailableException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public TableNotAvailableException(String string) {
		super(string);
	}
}
