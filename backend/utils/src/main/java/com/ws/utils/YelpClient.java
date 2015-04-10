package com.ws.utils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.security.InvalidKeyException;
import java.security.SignatureException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.ws.request_response.Coordinates;
import com.ws.request_response.POI;
import com.ws.utils.WebserviceInvoker;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class YelpClient{
	OAuthService service;
	Token accessToken;
	
	private static final String API_HOST = "api.yelp.com";
	private static final String SEARCH_PATH = "/v2/search";
	private static Logger log = Logger.getLogger(YelpClient.class.getName());
    private static final String YELP_TOKEN = "8aR4evnGjipVY7a_2NhjmLT_0LAhFOYV";
    private static final String YELP_TOKEN_SECRET = "wIbu7gLXjLXqjtSChGTs94p3d68";
    private static final String YELP_CONSUMER_SECRET = "x96giI9Qob0GWsf2NwJOnGqzuLw";
    private static final String YELP_CONSUMER_KEY = "TZZD5ojwiiyarcwvQNMH3Q";
    
    public YelpClient() {
    	this.service =
    			new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(YELP_CONSUMER_KEY)
    			.apiSecret(YELP_CONSUMER_SECRET).build();
    	this.accessToken = new Token(YELP_TOKEN, YELP_TOKEN_SECRET);
    }
    
    public List<POI> getPOIsFromYelp(String latitude, String longitude) throws Exception {
    	String cllString = latitude + "," +longitude;
    	return queryAPI(cllString);
	}
    
    private List<POI> queryAPI (String cllString) throws Exception{
    	String searchResponseJSON = searchForBusinessesByLocation(cllString);
    	log.info("Got business Response: " +searchResponseJSON);
    	JSONParser parser = new JSONParser();
    	JSONObject response = null;
    	try {
    		response = (JSONObject) parser.parse(searchResponseJSON);
    	} catch (ParseException pe) {
    		log.info("Error: could not parse JSON response:");
	    	throw pe;
    	}
    	
    	return getPOIsFromResponse(response);
    }

	private List<POI> getPOIsFromResponse(JSONObject response) {
		JSONArray businesses = (JSONArray) response.get("businesses");
    	log.info(String.format("%s businesses found",businesses.size()));
    	List<POI> poiList = new ArrayList<POI>();
    	for (int i = 0; i < businesses.size(); ++i){
    		JSONObject business = (JSONObject) businesses.get(i);
    		
    		POI poi = new POI();
    		
    		poi.setId(business.get("id").toString());
    		String businessName = business.get("name").toString();
    		poi.setName(businessName);
    		String rating = business.get("rating").toString();
    		poi.setRating(rating);
    		String webUrl = business.get("url").toString();
    		poi.setWebUrl(webUrl);
    		String address = ((JSONObject)business.get("location")).get("address").toString();
    		poi.setAddress(address);
    		String latitude = ((JSONObject)((JSONObject)business.get("location")).get("coordinate")).get("latitude").toString();
    		String longitude = ((JSONObject)((JSONObject)business.get("location")).get("coordinate")).get("longitude").toString();
    		
    		Coordinates coordinates = new Coordinates();
    		coordinates.setLatitude(latitude);
    		coordinates.setLongitude(longitude);
    		poi.setCoordinates(coordinates);
    		
    		poi.setAddress(address);
    		String reviewCount = business.get("review_count").toString();
    		poi.setReviewCount(Long.parseLong(reviewCount));
    		if(business.get("display_phone") != null){
	    		String phone = business.get("display_phone").toString();
	    		poi.setPhone(phone);
    		}
    		poi.setCategories(business.get("categories").toString());
    		
    		poiList.add(poi);
    	}
    	
    	return poiList;
	}

	private String searchForBusinessesByLocation(String cllString) {
		String queryParams = "?term=food&ll=" + cllString;
		OAuthRequest request = new OAuthRequest(Verb.GET, "http://" + API_HOST + SEARCH_PATH + queryParams);
		String response = sendRequestAndGetResponse(request);
		
		return response;
	}

	private String sendRequestAndGetResponse(OAuthRequest request) {
		this.service.signRequest(this.accessToken, request);
		Response response = request.send();
		return response.getBody();
	}
}

