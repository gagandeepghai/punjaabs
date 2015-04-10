package com.ws.utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.springframework.web.util.*;


public class TwitterClient{
	private static Logger log = Logger.getLogger(TwitterClient.class.getName());
	
	private static final String ACCESS_TOKEN = "access_token";
	private static final String BEARER = "bearer";
	private static final String TOKEN_TYPE = "token_type";
	private static final String BASIC = "Basic ";
	private static final String APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8 = "application/x-www-form-urlencoded;charset=UTF-8";
	private static final String CLIENT_CREDENTIALS = "client_credentials";
	private static final String GRANT_TYPE = "grant_type";
	
	private static final String BASE_URL = "https://api.twitter.com/1.1/search/";
	private static final String TWEET_SEARCH_TAG = "tweets.json?q=";
	private static final String AUTH_URL = "https://api.twitter.com/oauth2/token";
	private static final String TWITTER_API_KEY = "XaeSmkmzY57TwMqBE7UupEWA2";
	private static final String TWITTER_API_SECRET = "s75bfQpYO9kAe9lDZEFoy9gtM9HWCT9JiExIOU258d1pUCnyAy";
	private static final String TWTR_AUTH_HEADER = "Authorization";
	private static final String TWTR_CONTENT_HEADER = "Content-Type";	
	private static String TWITTER_BEARER;
	
	private static final String ENC = "UTF-8";
	
	public TwitterClient() throws Exception {
    	setBearer();
    }
    
    public String getHashTagInfo(String hashTag) throws Exception{
    	String completeUrl = getCompleteHashSearchUrl(hashTag);
    	System.out.println(completeUrl);
    	String bearerAuthToken = "Bearer " + TWITTER_BEARER;
    	OAuthRequest request = new OAuthRequest(Verb.GET, completeUrl);
		
		request.addHeader(TWTR_AUTH_HEADER, bearerAuthToken);
		request.addHeader(TWTR_CONTENT_HEADER, APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8);
		
		Response response = request.send();
		return response.getBody();
    }

	private String getCompleteHashSearchUrl(String hashTag) throws Exception {
		String completeUrl = BASE_URL + TWEET_SEARCH_TAG + hashTag;
		return UriUtils.encodeFragment(completeUrl, ENC);
	}

	public void setBearer() throws Exception {
		TWITTER_BEARER = getBearer();		
		System.out.println(TWITTER_BEARER);
	}

	private String getBearer() throws Exception {
		String encodedApiSecret = UriUtils.encodeFragment(TWITTER_API_SECRET, ENC);
    	String encodedApiKey = UriUtils.encodeFragment(TWITTER_API_KEY, ENC); 
    	
		String encodedSecret = encodedApiKey + ":" + encodedApiSecret;
		
		byte[] encodedBytes = Base64.encodeBase64(encodedSecret.getBytes());
		String encodedBase64Secret = new String(encodedBytes);
		
		encodedBase64Secret = BASIC + encodedBase64Secret;
		
		OAuthRequest request = new OAuthRequest(Verb.POST, AUTH_URL);
		
		request.addHeader(TWTR_AUTH_HEADER, encodedBase64Secret);
		request.addHeader(TWTR_CONTENT_HEADER, APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8);
		request.addBodyParameter(GRANT_TYPE, CLIENT_CREDENTIALS);
		
		Response response = request.send();
		
		return extractBearer(response);	
	}
    
    private String extractBearer(Response response) throws Exception {
    	JSONParser parser = new JSONParser();
    	JSONObject jsonResponse = null;
    	try {
    		jsonResponse = (JSONObject) parser.parse(response.getBody().toString());
    		String tokenType = (String) jsonResponse.get(TOKEN_TYPE);
    		if(tokenType.equalsIgnoreCase(BEARER)){
    			return (String) jsonResponse.get(ACCESS_TOKEN);
    		}else{
    			throw new Exception("No bearer in response");
    		}
    	} catch (Exception pe) {
    		log.info("Error: could not parse JSON response:");
	    	throw pe;
    	}
    	
	}
}

