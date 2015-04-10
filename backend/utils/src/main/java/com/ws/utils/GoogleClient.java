package com.ws.utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;


public class GoogleClient{
	private static Logger log = Logger.getLogger(GoogleClient.class.getName());
    
	private static final String BASE_URL = "maps.googleapis.com";
	private static final String SEARCH_PATH = "/maps/api/directions/xml";
	private static final String GOOGLE_API_KEY = "AIzaSyD3qe7ltpIJPfWy3a9JYKrWdBPH06wnek8";
    
    public GoogleClient() {
    }
    
    public String getDirections(String origin, String destination) throws Exception{
    	
    	String directionUrl = getUrl(origin, destination);
    	
    	log.info("Sending request for directions API to: " +directionUrl);
    	
    	WebserviceInvoker invoker = new WebserviceInvoker();
    	String response = invoker.sendGetRequest(directionUrl);
    	
    	log.info("Received response from directions API: " +response);
    	
    	return response;
    }

	private String getUrl(String origin, String destination) throws Exception {
		String queryParams = "key=" + GOOGLE_API_KEY + "&origin=" + origin + "&destination=" + destination;
		
		URI uri = new URI(
    			"https",
			    BASE_URL, 
			    SEARCH_PATH, 
			    queryParams,
			    null
			    );
		
		return uri.toURL().toString();
	}   
}

