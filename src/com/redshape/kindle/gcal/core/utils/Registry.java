package com.redshape.kindle.gcal.core.utils;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.util.Timer;
import com.amazon.kindle.kindlet.util.TimerTask;
import com.redshape.kindle.gcal.core.data.store.state.IStateManager;
import com.redshape.kindle.gcal.core.data.store.state.StateEvents;
import com.redshape.kindle.gcal.core.data.store.state.StateManager;
import com.redshape.kindle.gcal.core.event.AppEvent;
import com.redshape.kindle.gcal.core.event.EventType;
import com.redshape.kindle.gcal.core.event.IEventListener;
import com.redshape.kindle.gcal.core.net.AuthenticatedConnector;
import com.redshape.kindle.gcal.core.net.Connector;
import com.redshape.kindle.gcal.core.net.auth.ClientLogin;
import com.redshape.kindle.gcal.core.net.service.GoogleCalendarService;
import com.redshape.kindle.gcal.ui.ViewFacade;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.utils
 * @date 11/13/11 2:07 PM
 */
public final class Registry {
	private static final Logger log = Logger.getLogger( Registry.class );

	public static class Lock {
		private final Object lock = new Object();
		private int readers       = 0;
		private int writers       = 0;

		public void lockWrite() throws InterruptedException{
			while(readers > 0 || writers > 0){
				wait();
			}

			synchronized ( lock ) {
				writers++;
			}
		}

		public void unlockWrite() throws InterruptedException{
			synchronized ( lock ) {
				writers--;
			}
		}
	}

	public static final Lock ConnectivityLock = new Lock();

	public static class Events extends EventType {

		protected Events(String code) {
			super(code);
		}

		public static final Events Init = new Events("Registry.Events.Init");
	}

	public static class Attribute implements Serializable {
		private String name;
		private boolean restorable;

		protected Attribute( String name, boolean restorable ) {
			this.name = name;
			this.restorable = restorable;
		}

		public String name() {
			return this.name;
		}

		public boolean isRestorable() {
			return this.restorable;
		}

		public static final Attribute LastSync = new Attribute("Registry.Attribute.LastSync", true);
		public static final Attribute StateManager = new Attribute("Registry.Attribute.StateManager", false);
		public static final Attribute KindletContext = new Attribute("Registry.Attribute.KindletContext", false);
		public static final Attribute GoogleCalendarService = new Attribute("Registry.Attribute.GoogleCalendarService", false);
		public static final Attribute Connector = new Attribute("Registry.Attribute.Connector", false);
		public static final Attribute Service = new Attribute("Registry.Attribute.Service", false);
		public static final Attribute Login = new Attribute("Registry.Attribute.Login", true);
		public static final Attribute Password = new Attribute("Registry.Attribute.Password", true);
		public static final Attribute EntriesCount = new Attribute("Registry.Attribute.EntriesCount", true );
		public static final Attribute RefreshTime = new Attribute("Registry.Attribute.RefreshTime", true);
		public static final Attribute RefreshEnabled = new Attribute("Registry.Attribute.RefreshEnabled", true );

		public String toString() {
			return this.name();
		}

		public int hashCode() {
			return this.name().hashCode();
		}

		public boolean equals(Object obj) {
			return obj != null && obj instanceof Attribute
					&& (( Attribute ) obj).name().equals( this.name() );
		}
	}

	private static Map attributes = new HashMap();
	private static boolean initialized;
	private static Timer refreshTimer = new Timer();

	private static Timer timer = new Timer();

	public static Map attributesMap() {
		return new HashMap(attributes);
	}

	public static Timer getRefreshTimer() {
		return refreshTimer;
	}

	public static void requestConnectivity() {
		if ( !Registry.getContext().getConnectivity().isConnected() ) {
			try {
				Registry.getContext().getConnectivity().requestConnectivity(true);
			} catch ( Throwable e ) {}
		}
	}

	public static void cancelTimer() {
		refreshTimer.cancel();
		refreshTimer = new Timer();
	}

	public static void startTimer() {
		refreshTimer = new Timer();
	}

	public static void startStateFlushThread() {
		timer.scheduleAtFixedRate(
				new TimerTask() {
					public void run() {
						IStateManager manager = ( IStateManager ) Registry.get(Attribute.StateManager);
						if ( manager == null ) {
							log.warn("State manager not defined! State will not be saved after shutdown.");
							return;
						}

						try {
							log.info("Starting application state flush...");
							manager.save();
							log.info("Application state successfully flushed.");
							ViewFacade.hideDialogs();
							Registry.getContext().getProgressIndicator().setString("State restored successfully!");
						} catch ( IOException e ) {
							log.error(e.getMessage(), e);
						}
					}
				},
				0, 60000
		);
	}

	public synchronized static void init( final KindletContext context,
										  final IEventListener initHandler) {
		if ( initialized ) {
			return;
		}

		Registry.setContext(context);

		IStateManager manager = StateManager.getDefault();
		set( Attribute.StateManager, manager );

		manager.addListener(
				StateEvents.Save,
				new IEventListener() {
					public void handleEvent(AppEvent event) {
						Registry.getContext().getProgressIndicator().setString("State saved.");
					}
				}
		);

		manager.addListener( StateEvents.Restore,
				new IEventListener() {
					public void handleEvent(AppEvent event) {
						Registry.afterRestore( context, initHandler );
					}
				}
		);

		try {
			manager.restore();
		} catch ( IOException e ) {
			log.error( e.getMessage(), e );
			Registry.afterRestore( context, initHandler );
		}
	}

	private static void afterRestore( KindletContext context, IEventListener initHandler ) {
		Connector connector = new AuthenticatedConnector(
				context, new ClientLogin(context)
		);

		if ( Registry.get(Attribute.RefreshEnabled) == null ) {
			Registry.set(Attribute.RefreshEnabled, Boolean.FALSE);
		}

		if ( Registry.get(Attribute.RefreshTime) == null ) {
			Registry.set(Attribute.RefreshTime, new Long( Constants.TIME_MINUTE * 10 ) );
		}

		setConnector( connector );
		setService(new GoogleCalendarService());

		try {
			initHandler.handleEvent(new AppEvent(ViewFacade.Events.Init));
		} catch ( Throwable e ) {
			log.error( e.getMessage(), e );
		}

		initialized = true;
	}

	public static void setContext( KindletContext context ) {
		attributes.put( Attribute.KindletContext, context );
	}

	public static KindletContext getContext() {
		return (KindletContext) attributes.get( Attribute.KindletContext );
	}

	public static void set( Attribute attribute, Object value ) {
		attributes.put(attribute, value);
	}

	public static Object get( Attribute attribute ) {
		return attributes.get( attribute );
	}

	public static GoogleCalendarService getService() {
		return (GoogleCalendarService) attributes.get(Attribute.GoogleCalendarService);
	}

	public static void setService( GoogleCalendarService service ) {
		attributes.put( Attribute.GoogleCalendarService, service );
	}

	public static void setConnector( Connector service ) {
		attributes.put( Attribute.Connector, service );
	}

	public static Connector getConnector() {
		return (Connector) attributes.get( Attribute.Connector );
	}
}
