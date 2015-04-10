package com.ws.utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.springframework.web.util.UriUtils;

import java.security.InvalidKeyException;
import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class AmazonClient{
	private static Logger log = Logger.getLogger(AmazonClient.class.getName());
    
	private static final String AMAZON_ACCESS_ID = "AKIAIAG4FXDBQD35KJOA";
	private static final String AMAZON_SECRET_KEY = "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";
	private static final String AMAZON_ACCOUNT_EMAIL="khatri.astha@gmail.com";
	private static final String BASE_URL = "http://webservices.amazon.com";
	private static final String SEARCH_PATH = "/onca/xml";
	private static final String BASE_PARAM_STR = "Service=AWSECommerceService"
			+ "&Operation=ItemSearch"
			+ "&ResponseGroup=Small"
			+ "&SearchIndex=All";
	private static final String KEYWORD_STR="&Keywords=";
	private static final String ACCESS_KEY_STR="&AWSAccessKeyId=";
	private static final String TIMESTAMP_STR="&Timestamp=";
	private static final String SIGNATURE_STR="&Signature=";
	private static final String PREFIX_TO_SIGN = "GET\nwebservices.amazon.com\n/onca/xml\n";
	
	private static final String ENC = "UTF-8";
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA256";

	
	
	public static void main (String[] args){
		try{
			String response  = hitAmazon();
			log.info("Response: " +response);
		}catch(Exception ex){
			log.info("ERROR: " +ex.getMessage());
		}
	}
    
    public AmazonClient() {
    }
    
    public static String hitAmazon() throws Exception{
    	
    	String uri = generateURL();
    	
    	WebserviceInvoker invoker = new WebserviceInvoker();
    	String response = invoker.sendGetRequest(uri);
    	
    	return response;
    }

	private static String generateURL() throws Exception {
		String queryStr = BASE_PARAM_STR;
		queryStr += KEYWORD_STR +"tablet";
		queryStr += ACCESS_KEY_STR + AMAZON_ACCESS_ID;
		queryStr += TIMESTAMP_STR + getFormattedCurrentTime();
		URLEncoder.encode(queryStr, "UTF-8");
		
		String signature = getSign(queryStr);
		queryStr += SIGNATURE_STR + signature;
		
		String uri = BASE_URL + SEARCH_PATH + "?" + queryStr;
		
		return uri;
	}

	private static String getSign(String queryStr) throws Exception {
		String[] paramsSplit = queryStr.split("&");
		Arrays.sort(paramsSplit);
		
		String rejoinedParams = "";
		
		for (String str : paramsSplit){
			rejoinedParams += str;
			rejoinedParams += "&";
		}
		rejoinedParams = rejoinedParams.substring(0, rejoinedParams.length()-1);
		
		String strToSign = PREFIX_TO_SIGN + rejoinedParams;
		String signature = sign(strToSign);
		
		return signature;
	}

	private static String sign(String strToSign) throws Exception {
		SecretKeySpec signingKey = new SecretKeySpec(AMAZON_SECRET_KEY.getBytes(), HMAC_SHA1_ALGORITHM);

		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		mac.init(signingKey);
		
		byte[] rawHmac = mac.doFinal(strToSign.getBytes());

		byte[] result = Base64.encodeBase64(rawHmac);
		
		String retVal = new String(result);
		return URLEncoder.encode(retVal, "UTF-8");
	}

	private static String getFormattedCurrentTime() {
		String pattern = "YYYY-MM-dd'T'hh:mm:ss'Z'";
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		String dateTime = dateFormat.format(new Date());
		
		return dateTime;
	}
}

