package com.ws.utils;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;
/**
* Generic service provider for two-step OAuth10a.
*/
public class TwoStepOAuth extends DefaultApi10a {
	@Override
	public String getAccessTokenEndpoint() {
		return null;
	}
	
	public String getAuthorizationUrl(Token arg0) {
		return null;
	}
	@Override
	public String getRequestTokenEndpoint() {
		return null;
	}
}


