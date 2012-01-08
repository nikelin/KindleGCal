package com.redshape.kindle.gcal.core.net.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.net.http
 * @date 11/17/11 2:24 PM
 */
public class HttpRequest {
	public static final String POST = "POST";
	public static final String GET = "GET";
	public static final String HEAD = "HEAD";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";

	private URI uri;
	private String method;
	private Map headers = new HashMap();
	private Map parameters = new HashMap();
	private String body;

	public HttpRequest( URI uri ) {
		this.uri = uri;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Map getHeaders() {
		return this.headers;
	}

	public void setHeader( String name, Object value ) {
		this.headers.put(name, value);
	}

	public Object getHeader( String name ) {
		return this.headers.get(name);
	}

	public Map getParameters() {
		return this.parameters;
	}

	public void setParameter( String name, Object value ) {
		this.parameters.put(name, value);
	}

	public Object getParameter( String name ) {
		return this.parameters.get(name);
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public URI prepareURI() throws URISyntaxException {
		StringBuffer builder = new StringBuffer();
		builder.append( this.getUri().toString() );

		Iterator iterator = this.getParameters().entrySet().iterator();
		while ( iterator.hasNext() ) {
			Map.Entry entry = (Map.Entry) iterator.next();

			builder.append( entry.getKey() ).append("=").append( entry.getValue() );

			if ( iterator.hasNext() ) {
				builder.append("&");
			}
		}

		return new URI(builder.toString());
	}

}
