package com.punjaabs.reservation.exception;

public class InvalidReservationTimeException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public InvalidReservationTimeException(String string) {
		super(string);
	}
}
