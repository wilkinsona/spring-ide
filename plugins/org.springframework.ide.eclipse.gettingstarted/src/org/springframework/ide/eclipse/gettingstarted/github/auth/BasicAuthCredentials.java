/*******************************************************************************
 *  Copyright (c) 2013 VMware, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *      VMware, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.gettingstarted.github.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.ide.eclipse.gettingstarted.GettingStartedActivator;
import org.springframework.web.client.RestTemplate;

/**
 * Uses basic authentication username and passwd to access github rest api.
 * 
 * @author Kris De Volder
 */
public class BasicAuthCredentials extends Credentials {

	private String username;
	private String passwd;

	public BasicAuthCredentials(String username, String passwd) {
		this.username = username;
		this.passwd = passwd;
	}
	
	@Override
	public String toString() {
		return "BasicAuthCredentials("+username+")";
	}

	private String computeAuthString() throws UnsupportedEncodingException {
		String authorisation = username + ":" + passwd;
		byte[] encodedAuthorisation = Base64.encodeBase64(authorisation.getBytes("utf8"));
		String authString = "Basic " + new String(encodedAuthorisation);
		return authString;
	}
	
	@Override
	public RestTemplate apply(RestTemplate rest) {
		List<ClientHttpRequestInterceptor> interceptors = rest.getInterceptors();
		interceptors.add(new ClientHttpRequestInterceptor() {
			public ClientHttpResponse intercept(HttpRequest request, byte[] body,
					ClientHttpRequestExecution execution) throws IOException {
				HttpHeaders headers = request.getHeaders();
				if (!headers.containsKey("Authorization")) {
					String authString = computeAuthString();
					headers.add("Authorization", authString);
				}
				return execution.execute(request, body);
			}

		});
		return rest;
	}

	@Override
	public void apply(URLConnection conn) {
		try {
			conn.setRequestProperty("Authorization", computeAuthString());
		} catch (UnsupportedEncodingException e) {
			//Shouldn't really be possible...
			GettingStartedActivator.log(e);
		}
	}

}