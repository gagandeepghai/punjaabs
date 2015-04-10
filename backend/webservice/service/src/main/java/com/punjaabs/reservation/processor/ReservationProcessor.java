package com.punjaabs.reservation.processor;

import com.dev.persistence.dao.Reservations;
import com.punjaabs.reservation.request_response.ReservationRequest;
import com.punjaabs.reservation.request_response.ReservationResponse;

public class ReservationProcessor {
	
	private ReservationRequest request;
	
	public ReservationProcessor(ReservationRequest request){
		this.request = request;
	}
	
	public ReservationProcessor(){
		
	}

	public void printAvailability() {
		TableManager.getInstance().dumpAvailabilty();
	}
	
	public ReservationResponse reserve() throws Exception{
		int tableId = TableManager.getInstance().bookSlot(request.getReservationTime());		
		ReservationResponse response = createReservation(tableId);		
		EmailHandler.sendEmailForReservationConfirmation(request, response.getReservationId(), request.getNumberOfPeople());
		
		return response;
	}

	private ReservationResponse createReservation(int tableId) throws Exception {
		Reservations reservation = new Reservations();
		
		reservation.setTableId(tableId);
		reservation.setStartTime(request.getReservationTime());
		reservation.setUserEmail(request.getUserEmail());
		reservation.setUserName(request.getUserName());
		reservation.setUserPhone(request.getUserPhone());
		
		reservation.save();
		
		return createReservationResponse(reservation);
	}
	
	private ReservationResponse createReservationResponse(Reservations reservation){
		
		ReservationResponse response = new ReservationResponse();
		response.setTableId(reservation.getTableId());
		response.setReservationId(reservation.getReservationId());
		response.setMessage("Success");
		
		return response;		
	}

}
