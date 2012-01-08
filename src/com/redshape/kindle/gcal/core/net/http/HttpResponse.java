package com.redshape.kindle.gcal.core.net.http;

import com.redshape.kindle.gcal.core.utils.StreamUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.net.http
 * @date 11/17/11 2:29 PM
 */
public class HttpResponse {
	private static final Logger log = Logger.getLogger(HttpResponse.class);

	private String method;
	private URI uri;
	private Map headers = new HashMap();
	private int code;
	private String body;
	private Map parameters = new HashMap();

	public void setParameter( String name, Object value ) {
		this.parameters.put(name, value);
	}

	public Object getParameter( String name ) {
		return this.parameters.get(name);
	}

	public Map getParameters() {
		return this.parameters;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Map getHeaders() {
		return this.headers;
	}

	public Object getHeader( String name ) {
		return this.headers.get(name);
	}

	public void setHeader( String name, Object value ) {
		this.headers.put(name, value);
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public static HttpResponse valueOf( HttpURLConnection connection ) throws URISyntaxException,
																				IOException {
		String data;
		try {
			data = StreamUtils.readToString(connection.getInputStream());
		} catch ( IOException e ) {
			data = StreamUtils.readToString( connection.getErrorStream() );
			if ( !data.startsWith("{") ) {
				throw new IOException( data, e );
			}
		}

		data = data.trim();

		HttpResponse response = new HttpResponse();
		response.setBody( data );
		response.setCode( connection.getResponseCode() );

		log.info("Response code: " + connection.getResponseCode() );
		log.info("Response data: " + data );

		if ( !( data.startsWith("<") || data.startsWith("{") ) ) {
			String[] params = data.split("&");
			for ( int i = 0; i < params.length; i++ ) {
				String[] paramParts = params[i].split("=");
				response.setParameter(paramParts[0], paramParts[1]);
			}
			// response.setCode( connection.getResponseCode() );
		}

		response.headers = connection.getHeaderFields();

		return response;
	}
}
