package com.punjaabs.reservation.processor;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.punjaabs.reservation.exception.InvalidReservationTimeException;

public class Table {
	
	private static Logger log = Logger.getLogger(Table.class
			.getName());
	private static final int TIME_SLOTS = 16;
	private Integer id;
	
	private boolean[] availabilityArray = new boolean[TIME_SLOTS];
	private static Map<String, Integer> timeIndexMap;
	
	static{
		timeIndexMap = new TreeMap<String, Integer>();
		timeIndexMap.put("1700", 0);
		timeIndexMap.put("1715", 1);
		timeIndexMap.put("1730", 2);
		timeIndexMap.put("1745", 2);
		timeIndexMap.put("1800", 3);
		timeIndexMap.put("1815", 4);
		timeIndexMap.put("1830", 5);
		timeIndexMap.put("1845", 6);
		timeIndexMap.put("1900", 7);
		timeIndexMap.put("1915", 8);
		timeIndexMap.put("1930", 9);
		timeIndexMap.put("1945", 10);
		timeIndexMap.put("2000", 11);
		timeIndexMap.put("2015", 12);
		timeIndexMap.put("2030", 13);
		timeIndexMap.put("2045", 14);
		timeIndexMap.put("2100", 15);		
	}
	
	public Table(Integer Id){
		this.id = Id;
		init();
	}
	
	public Integer getId(){
		return id;
	}
	
	private void init(){
		for (int i = 0 ; i <TIME_SLOTS ; ++i){
			availabilityArray[i] = true;
		}
	}
	
	public boolean isAvailableForSlot(String time) throws InvalidReservationTimeException{
		log.info("Checking availablity [" + time + "] for Table: " +id);
		if(timeIndexMap.containsKey(time)){
			int timeIndex = timeIndexMap.get(time);
			for (int i = timeIndex; i< (timeIndex + 4); ++i){
				if(availabilityArray[i] == false){
					return false;
				}
			}
			for (int i = timeIndex; i< (timeIndex + 4); ++i){
				availabilityArray[i] = false;		
			}
			return true;
			
		}else{
			throw new InvalidReservationTimeException("Invaid time asked for booking: " + time);
		}
	}

	public void printAvailability() {
		log.info("Availability for Table: " +id);
		int i = 0;
		for(String timeKey : timeIndexMap.keySet()){
			log.info("\tHour(" + timeKey + ")" + ": " + availabilityArray[i++]);
		}
		
	}
}
