package com.ws.utils;

import java.util.logging.Logger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

public class WebserviceInvoker 
{ 
	private static final Logger logger = Logger.getLogger(WebserviceInvoker.class.getName());
	
	private static final String REQUEST_METHOD_TYPE_POST = "POST"; 
	private static final String REQUEST_METHOD_TYPE_GET = "GET"; 
    private static final int HTTP_OK = 200;
    private static final int HTTP_REDIRECT = 300;
    private static final String CONTENT_TYPE              = "Content-type";
    private static final String CONTENT_TYPE_VALUE        = "application/xml";
    public static final String RESPONSE        = "RESPONSE";
    public static final String RESPONSE_CODE        = "RESPONSE_CODE";
    private Integer responseCode = null;
    
    private void setConnProperties( HttpURLConnection conn, Hashtable<String,String> properties) {
        if ( properties != null) {
            Set<String> set = properties.keySet();
            if ( set != null) {
                Iterator<String> itr = set.iterator();
                while ( itr.hasNext()) {
                  String key = itr.next();
                  String value = properties.get( key);
                  logger.info("Setting " +key +" to: " +value);
                  conn.setRequestProperty( key, value);
                }
            }
        }
    }
    
    private HttpURLConnection connect( String destinationURL, Hashtable<String,String> properties, boolean disableCertCheck, String requestType) throws MalformedURLException, IOException {
    	HttpURLConnection  conn = null;

        try {
            URL url = new URL ( destinationURL);

            conn = ( HttpURLConnection) url.openConnection();

        	conn.setRequestMethod( requestType);
            setConnProperties( conn, properties);
            conn.setDoOutput( true);            
            conn.connect();
        }
        catch ( ProtocolException e) {
        	logger.severe( "Connection does not recognize POST: " + e);
        }

        return conn;
    }


    private void send( OutputStream outputStream, String content) throws IOException {
        OutputStreamWriter wr = new OutputStreamWriter( outputStream);

        wr.write( content);
        wr.flush();
        wr.close ();
    }


    private String receive( InputStream inputStream) throws IOException {
        StringBuffer response = new StringBuffer();
        BufferedReader input = new BufferedReader( new InputStreamReader(inputStream));
        String line = null;

        while ( null != ( line = input.readLine())) {    
            response.append( line);    
        }    
        input.close();

        return response.toString();
    }


    public String sendRequest( String destinationUrl, Hashtable<String,String> properties, String inputXml, boolean disableCertCheck, String requestType)
        throws MalformedURLException, IOException
    {
    	HttpURLConnection urlConn = null;
        String  response = null;                

        System.setProperty( "sun.net.http.allowRestrictedHeaders", "true");
        
        logger.info( "sendHttpsRequest Dumping request message: \n%s" + inputXml + " , destination url = " + destinationUrl);
        try {
            urlConn = connect( destinationUrl, properties, disableCertCheck, requestType);
            if ( urlConn != null) {
            	send( urlConn.getOutputStream(), inputXml); 
            	responseCode = urlConn.getResponseCode();
            	if(responseCode >= HTTP_OK && responseCode < HTTP_REDIRECT) {
            		response = receive(urlConn.getInputStream());
            	} else {
            	    response = receive(urlConn.getErrorStream());
            	}
            }
        }
        finally {
        	if ( urlConn != null) {
        		urlConn.disconnect();
        	}
        }

        return response;       
    }
    
    public String sendGetRequest(String endUrl) throws Exception {
		String responseXml = "";
		
		logger.info("Sending request to EndUrl: " + endUrl);
		Hashtable<String, String> properties = new Hashtable<String, String>();
		
		responseXml = sendRequest(endUrl, properties, "", false, REQUEST_METHOD_TYPE_GET);
		logger.info("Response XML: " + responseXml);
		
		return responseXml;
	}
    
    private HttpURLConnection httpConnect( String destinationURL) 
            throws MalformedURLException, IOException {
       
        HttpURLConnection  conn = null;

        try {
            URL url = new URL ( destinationURL);
            conn = ( HttpURLConnection) url.openConnection();
            conn.setRequestMethod( REQUEST_METHOD_TYPE_POST);
            conn.setDoOutput( true); 
            conn.setRequestProperty( CONTENT_TYPE, CONTENT_TYPE_VALUE);
            conn.connect();
        }
        catch ( ProtocolException e) {
            logger.severe( "Connection does not recognize POST: " + e);
        }

        return conn;
    }
    
    public String sendHttpRequest( String destinationUrl, String inputXml)
            throws MalformedURLException, IOException
    {
        HttpURLConnection urlConn = null;
        String  response = null;
        
        logger.info( "sendHttpRequest Dumping request message: \n" + inputXml + " , destination url = " + destinationUrl);

        try {
            urlConn = httpConnect( destinationUrl);
            if ( urlConn != null) {
                send( urlConn.getOutputStream(), inputXml); 
                responseCode = urlConn.getResponseCode();
                if(responseCode >= HTTP_OK && responseCode < HTTP_REDIRECT) {
                    response = receive(urlConn.getInputStream());
                } else {
                    response = receive(urlConn.getErrorStream());
                }
            }
        }
        finally {
            if ( urlConn != null) {
                urlConn.disconnect();
            }
        }

        return response;       
    }
    
    public Integer getResponseCode() {
    	return responseCode;
    }

    public String sendRequest( String destinationURL, Hashtable<String,String> properties, String content)
        throws MalformedURLException, IOException
    {
    	return sendRequest( destinationURL, properties, content, false, REQUEST_METHOD_TYPE_POST);
    }
    
    public static Map<String, String> invoke(String endUrl, String requestXml) 
            throws MalformedURLException, IOException {  
 	   
 	   logger.info("Sending request to EndUrl: " + endUrl + ", for request: " + requestXml);
 	   Hashtable<String, String> properties = new Hashtable<String, String>();
 	   properties.put(CONTENT_TYPE,CONTENT_TYPE_VALUE);
 	   
 	   WebserviceInvoker invoker = new WebserviceInvoker();
 	   String responseXml = invoker.sendRequest(endUrl, properties, requestXml);
 	   logger.info("Response XML: " + responseXml);
 	   Map<String, String> map = new HashMap<String, String>();
 	   map.put(WebserviceInvoker.RESPONSE, responseXml);
 	   map.put(WebserviceInvoker.RESPONSE_CODE, invoker.responseCode+"");
 	   return map;
    }
}