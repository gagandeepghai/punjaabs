package com.punjaabs.reservation.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.punjaabs.reservation.exception.TableNotAvailableException;

public class TableManager {
	private static final String AUSTRALIA_HOBART_TZ = "Australia/Hobart";
	private static Logger log = Logger.getLogger(TableManager.class
			.getName());
	private static final int FOUR_SEATER_COUNT = 10;
	private static List<Table> tableInventory;
	private static DateTime LOAD_TIME;
	
	private static TableManager instance = null;
	
	public static TableManager getInstance(){
		if(instance == null){
			instance =  new TableManager();
		}
		
		DateTimeZone zone = DateTimeZone.forID(AUSTRALIA_HOBART_TZ);
		DateTime timeNow = new DateTime(zone);
		
		log.info("Now: " +timeNow.getDayOfYear());
		log.info("Loaded: " +LOAD_TIME.getDayOfYear());
		
		if(timeNow.getDayOfYear() > LOAD_TIME.getDayOfYear()){
			purgeAndRecreate();
		}
		
		return instance;
	}
	
	private TableManager(){
		purgeAndRecreate();
	}

	private static void purgeAndRecreate() {
		log.info("Purging and Initializing TableManager");
		tableInventory = new ArrayList<Table>();
		for(int i=0;i<FOUR_SEATER_COUNT;++i){
			tableInventory.add(new Table(i+1));
		}		
		DateTimeZone zone = DateTimeZone.forID(AUSTRALIA_HOBART_TZ);
		LOAD_TIME = new DateTime(zone);
	}
	
	public void dumpAvailabilty(){
		for (Table table : tableInventory){
			table.printAvailability();
		}
	}

	public int bookSlot(String time) throws Exception {
		Table tablePicked = null;
		for (Table table : tableInventory){
			if(table.isAvailableForSlot(time)){
				tablePicked = table;
				break;
			}
		}
		
		if(tablePicked == null){
			throw new TableNotAvailableException("No table available at [" + time + "]. Please try some other time slot");
		}
		
		return tablePicked.getId();
		
	}
}
