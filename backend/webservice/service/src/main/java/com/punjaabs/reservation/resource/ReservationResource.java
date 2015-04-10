package com.punjaabs.reservation.resource;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.dev.persistence.dbaccess.PersistenceManager;
import com.dev.persistence.dbaccess.PersistenceManagerFactory;
import com.punjaabs.reservation.exception.TableNotAvailableException;
import com.punjaabs.reservation.processor.ReservationProcessor;
import com.punjaabs.reservation.request_response.ReservationRequest;
import com.punjaabs.reservation.request_response.ReservationResponse;

import org.apache.catalina.filters.CorsFilter;

@Path("/reservation")
public class ReservationResource {
	private static final String WS_RETURN_TYPE_JSON = "application/json";
	private static Logger log = Logger.getLogger(ReservationResource.class.getName());
	
	@POST
	@Path("/enterReservation")
	@Consumes({ "application/xml", "application/json" })
	public Response enterReservation(ReservationRequest request){
		CorsFilter filter = new CorsFilter();
		PersistenceManager persist = null;
		ReservationResponse response;
		try{
			log.info("Filter: " +filter);
			log.info("Received request: " +request.marshal());
			persist = PersistenceManagerFactory.getInstance()
					.getPersistenceManager();
			persist.beginTransaction();
			
			ReservationProcessor processor = new ReservationProcessor(request);
			response = processor.reserve();
			
			persist.commitTransaction();
			
			return Response.ok().type(WS_RETURN_TYPE_JSON).entity(response).build();
		}catch(TableNotAvailableException ex){
			response = new ReservationResponse();
			response.setMessage(ex.getMessage());
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(response)
					.type(WS_RETURN_TYPE_JSON)
					.build();
		}catch(Exception ex){
			ex.printStackTrace();
			log.info("ERROR: " +ex.getMessage());
			
			response = new ReservationResponse();
			response.setMessage("Unknown error occurred");
						
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(response)
					.type(WS_RETURN_TYPE_JSON)
					.build();
		} finally {
			if (persist != null) {
				persist.rollbackTransaction();
				persist.cleanUp();
			}
		}	
	}
	
	@GET
	@Path("/printAvailability")
	@Consumes({ "application/xml", "application/json" })
	public Response printAvailability(){
		try{
			ReservationProcessor processor = new ReservationProcessor();
			processor.printAvailability();
		}catch(Exception ex){
			log.info("ERROR");
		}
		
		return  Response.ok().type(WS_RETURN_TYPE_JSON).build();
	}
}
