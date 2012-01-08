package com.redshape.kindle.gcal.core.net;

import com.amazon.kindle.kindlet.KindletContext;
import com.redshape.kindle.gcal.core.event.AppEvent;
import com.redshape.kindle.gcal.core.event.EventDispatcher;
import com.redshape.kindle.gcal.core.event.EventType;
import com.redshape.kindle.gcal.core.event.IEventListener;
import com.redshape.kindle.gcal.core.net.http.HttpRequest;
import com.redshape.kindle.gcal.core.net.http.HttpResponse;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.net
 * @date 11/17/11 2:36 PM
 */
public class Connector extends EventDispatcher implements IConnector {
	private static final Logger log = Logger.getLogger(Connector.class);

	public static class Events extends EventType {

		protected Events( String code ) {
			super(code);
		}

		public static final Events Response = new Events("Connector.Events.Response");

	}

	private KindletContext context;

	public Connector(KindletContext context) {
		this.context = context;
	}

	public void request(HttpRequest request) throws IOException {
		this.request(request, null);
	}

	public void request(HttpRequest request, IEventListener listener ) throws IOException {
		try {
			log.info( "Requesting " + request.prepareURI() );

			URL url = request.prepareURI().toURL();
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("GData-Version", "2.0");
			connection.setRequestProperty("Content-Length", "0");
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod(
				request.getMethod() == null ? "GET" : request.getMethod() );

			Iterator entriesIterator = request.getHeaders().entrySet().iterator();
			while ( entriesIterator.hasNext() ) {
				Map.Entry entry = (Map.Entry) entriesIterator.next();
				if ( entry.getKey() == null || entry.getValue() == null ) {
					continue;
				}

				log.info("Header: (" + entry.getKey() + ":" + entry.getValue() + ")");

				connection.setRequestProperty(entry.getKey().toString(), entry.getValue().toString());
			}

			try {
				connection.connect();
			} catch ( IOException e ) {
				log.error( e.getMessage(), e );
				log.info("Http response: " + connection.getResponseCode() );
				log.info("Http body" + connection.getResponseMessage() );

				throw e;
			}

			AppEvent event = new AppEvent( Connector.Events.Response, new Object[] {
				HttpResponse.valueOf(connection)
			});

			if ( listener == null ) {
				this.dispatch( event );
			} else {
				listener.handleEvent(event);
			}
		} catch ( Throwable e ) {
			log.error( e.getMessage(), e );
			throw new IOException( e.getMessage(), e );
		}
	}
}
