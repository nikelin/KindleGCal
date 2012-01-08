package com.redshape.kindle.gcal.core.net.service;

import com.redshape.kindle.gcal.core.event.AppEvent;
import com.redshape.kindle.gcal.core.event.EventDispatcher;
import com.redshape.kindle.gcal.core.event.EventType;
import com.redshape.kindle.gcal.core.event.IEventListener;
import com.redshape.kindle.gcal.core.net.http.HttpRequest;
import com.redshape.kindle.gcal.core.net.http.HttpResponse;
import com.redshape.kindle.gcal.core.utils.Registry;
import com.redshape.kindle.gcal.data.Author;
import com.redshape.kindle.gcal.data.Calendar;
import com.redshape.kindle.gcal.data.Entry;
import com.redshape.kindle.gcal.ui.Dispatcher;
import com.redshape.kindle.gcal.ui.ViewFacade;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.net.service
 * @date 11/20/11 1:04 PM
 */
public class GoogleCalendarService extends EventDispatcher {
	public static class Events extends EventType {
		protected Events( String code ) {
			super(code);
		}

		public static final Events Calendars = new Events("GoogleCalendarService.Events.Calendars");
		public static final Events CalendarsLoadingFail = new Events("GoogleCalendarService.Events.CalendarsLoadingFail");
		public static final Events Events = new Events("GoogleCalendarService.Events.Events");
		public static final Events EventsLoadingFail = new Events("GoogleCalendarService.Events.EventsLoadingFail");

	}

	public static String calendarsUri =
			"https://www.google.com/calendar/feeds/default/allcalendars/full?alt=json";

	private DateFormat dateFormat;
	private JSONParser parser;

	public GoogleCalendarService() {
		this( new JSONParser(), new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ") );
	}

	public GoogleCalendarService(JSONParser parser, DateFormat format) {
		this.parser = parser;
		this.dateFormat = format;
	}

	public void retrieveCalendars() throws IOException {
		try {
			String login = (String) Registry.get( Registry.Attribute.Login );
			if ( login == null ) {
				ViewFacade.showInputDialog("Auth", "Enter your Google ID:", new IEventListener() {
					public void handleEvent(final AppEvent event) {
						final String login = (String) event.getArg(0);
						Registry.set( Registry.Attribute.Login, login );

						EventQueue.invokeLater(new Runnable() {
							public void run() {
								try {
									GoogleCalendarService.this.requestCalendarsList(login);
								} catch ( Throwable e ) {
									ViewFacade.showError("Internal exception", "Calendars list loading failed");
									throw new RuntimeException( e );
								}
							}
						});
					}
				});
			} else {
				this.requestCalendarsList(login);
			}
		} catch ( URISyntaxException e ) {
			this.dispatch( Events.CalendarsLoadingFail );
			throw new IOException("URI syntax exception", e );
		} catch ( ParseException e ) {
			this.dispatch( Events.CalendarsLoadingFail );
			throw new IOException( "Response parsing exception", e );
		}
	}

	protected void requestCalendarsList( String login )
		throws IOException, ParseException, URISyntaxException {
		HttpRequest request = new HttpRequest(
			new URI(calendarsUri)
		);

		request.setMethod("GET");

		Registry.getConnector().request(request, new IEventListener() {
			public void handleEvent(AppEvent event) {
				try {
					List result = GoogleCalendarService.this.processCalendarsResponse( (HttpResponse) event.getArg(0) );
					Registry.set(Registry.Attribute.LastSync, new Date() );
					Dispatcher.get().dispatch(
							new AppEvent(Events.Calendars, new Object[]{ result })
					);;
				} catch ( Throwable e ) {
					ViewFacade.showError("Internal exception", e.getMessage() );
					throw new RuntimeException( e );
				}
			}
		});
	}

	protected List processCalendarsResponse( HttpResponse response ) throws ParseException  {
		List result = new ArrayList();

		JSONObject object = (JSONObject) this.parser.parse( response.getBody() );
		if ( object.containsKey("error") ) {
			this.processError(object);
			return new ArrayList();
		}

		JSONObject feed = (JSONObject) object.get("feed");

		JSONArray items = (JSONArray) feed.get("entry");
		if ( items == null ) {
			ViewFacade.showError("Error", "Failed to recognize protocol data format");
			return new ArrayList();
		}

		Iterator itemsIterator = items.iterator();
		while( itemsIterator.hasNext() ) {
			result.add( this.processCalendarItem( (JSONObject) itemsIterator.next() ) );
		}

		return result;
	}

	protected Calendar processCalendarItem( JSONObject object ) {
		Calendar calendar = new Calendar();
		calendar.setId((( JSONObject ) object.get("id")).get("$t").toString());
		calendar.setSelfLink( ( (JSONObject) object.get("content") ).get("src").toString() );
		calendar.setKind( object.get("gd$kind").toString() );

		JSONArray authorsList = ( (JSONArray) object.get("author") );
		if ( !authorsList.isEmpty() ) {
			JSONObject authorObject = (JSONObject) authorsList.get(0);
			if ( authorObject.containsKey("email") ) {
				Author author = new Author();
				author.setEmail( ( (JSONObject) authorObject.get("email") ).get("$t").toString() );
				author.setDisplayName( ( (JSONObject) authorObject.get("name") ).get("$t").toString() );
				calendar.setAuthor(author);
			}
		}

		calendar.setSummary((( JSONObject ) object.get("title")).get("$t").toString());
		calendar.setEtag( object.get("gd$etag").toString() );

		return calendar;
	}

	public void retrieveEvents( Calendar calendar ) throws IOException {
		try {
			HttpRequest request = new HttpRequest(
				new URI( calendar.getSelfLink() + "?alt=json")
			);
			request.setMethod("GET");

			Registry.getConnector().request(request, new IEventListener() {
				public void handleEvent(AppEvent event) {
					try {
						List result = new ArrayList();
						Registry.set(Registry.Attribute.LastSync, new Date() );

						HttpResponse response = (HttpResponse) event.getArg(0);
						JSONObject responseData = (JSONObject) GoogleCalendarService.this.
																		parser.parse(response.getBody());
						if ( responseData.containsKey("error") ) {
							GoogleCalendarService.this.processError( responseData );
							return;
						}

						JSONObject feedObject = (JSONObject) responseData.get("feed");
						JSONArray itemsList = (JSONArray) feedObject.get("entry");
						Iterator itemsIterator = itemsList.iterator();
						while ( itemsIterator.hasNext() ) {
							result.add( GoogleCalendarService.this.processEventItem(( JSONObject ) itemsIterator.next()) );
						}

						Dispatcher.get().dispatch(
								new AppEvent(
										Events.Events,
										new Object[]{ result }
								)
						);
					} catch ( Throwable  e ) {
						ViewFacade.showError("Internal exception", e.getMessage() );
						throw new RuntimeException( e );
					}
				}
			});
		} catch ( URISyntaxException e ) {
			this.dispatch( Events.EventsLoadingFail );
			throw new IOException( e.getMessage(), e );
		}
	}

	protected void processError( JSONObject object ) {
		JSONObject error = ( (JSONObject) object.get("error") );

		ViewFacade.showError(
			"Error " + error.get("code").toString(),
			(String) error.get("message")
		);
	}

	protected Date parseDate( String date ) throws java.text.ParseException {
		/**
		 * Sometimes instead of correct timezone value, we
		 * receive simple 'Z' value which brock date format
		 * recognizing by java.text.SimpleDateFormat
		 */
		if ( date.contains("Z") ) {
			date = date.replace("Z", "+0300");
		}

		/**
		 * FIX due to timezone format differences between RFC2333 and GData
		 */
		int plusIdx = date.indexOf("+");
		int tzSymb = date.indexOf( ":", plusIdx );
		if ( tzSymb != - 1 ) {
			String first = date.substring( 0, tzSymb );
			String second = date.substring( tzSymb + 1 );
			date = first + second;
		}

		return this.dateFormat.parse( date  );
	}

	protected Entry processEventItem( JSONObject object ) throws java.text.ParseException {
		Entry entry = new Entry();
		entry.setId( ( (JSONObject) object.get("id") ).get("$t").toString() );
		entry.setSummary( ( (JSONObject) object.get("title") ).get("$t").toString() );
		entry.setEtag( object.get("gd$etag").toString() );

		JSONArray authorsList = ( (JSONArray) object.get("author") );
		if ( !authorsList.isEmpty() ) {
			JSONObject authorObject = (JSONObject) authorsList.get(0);
			if ( authorObject.containsKey("email") ) {
				Author author = new Author();
				author.setEmail( ( (JSONObject) authorObject.get("email") ).get("$t").toString() );
				author.setDisplayName( ( (JSONObject) authorObject.get("name") ).get("$t").toString() );
				entry.setAuthor(author);
			}
		}

		JSONArray locationsList = (JSONArray) object.get("gd$where");
		if ( !locationsList.isEmpty() ) {
			JSONObject location = (JSONObject) locationsList.get(0);
			if ( location.containsKey("valueString") ) {
				entry.setLocation( location.get("valueString").toString() );
			}

		}

		entry.setKind( object.get("gd$kind").toString() );
		entry.setCreated( this.parseDate( ( (JSONObject) object.get("published") ).get("$t").toString() ) );
		entry.setUpdated( this.parseDate( ( (JSONObject) object.get("updated") ).get("$t").toString() ));

		JSONArray timeList = (JSONArray) object.get("gd$when");
		if ( !timeList.isEmpty() ) {
			JSONObject whenField = (JSONObject) timeList.get(0);

			if ( whenField.containsKey("startTime")
					&& whenField.containsKey("endTime") ) {
				entry.setStartDate( this.parseDate( whenField.get("startTime").toString() ) );
				entry.setEndDate( this.parseDate( whenField.get("endTime").toString() ) );
			}
		}

		return entry;
	}

}
