package com.redshape.kindle.gcal.data.loaders;

import com.amazon.kindle.kindlet.net.ConnectivityHandler;
import com.amazon.kindle.kindlet.net.NetworkDisabledDetails;
import com.redshape.kindle.gcal.core.data.store.loaders.AbstractDataLoader;
import com.redshape.kindle.gcal.core.data.store.loaders.LoaderEvent;
import com.redshape.kindle.gcal.core.data.store.loaders.LoaderException;
import com.redshape.kindle.gcal.core.event.AppEvent;
import com.redshape.kindle.gcal.core.event.IEventListener;
import com.redshape.kindle.gcal.core.net.service.GoogleCalendarService;
import com.redshape.kindle.gcal.core.utils.Registry;
import com.redshape.kindle.gcal.data.Calendar;
import com.redshape.kindle.gcal.ui.Dispatcher;
import org.apache.log4j.Logger;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.data.store
 * @date 11/17/11 2:01 PM
 */
public class EventsLoader extends AbstractDataLoader {
	private static final Logger log = Logger.getLogger( EventsLoader.class );

	private Calendar calendar;

	public EventsLoader( Calendar calendar ) {
		this.calendar = calendar;
		this.init();
	}

	protected void init() {
		Dispatcher.get().addListener(
			GoogleCalendarService.Events.Events,
			new IEventListener() {
				public void handleEvent(AppEvent event) {
					EventsLoader.this.dispatch(LoaderEvent.BeforeLoad);
					EventsLoader.this.dispatch(
							new AppEvent(
									LoaderEvent.Loaded,
									new Object[]{ event.getArg(0) }
							)
					);
				}
			}
		);
	}

	protected void onConnected() {
		try {
			Registry.getService().retrieveEvents(EventsLoader.this.calendar);
		} catch ( Throwable e ) {
			Registry.getContext().getProgressIndicator().setString("Unable to update events list");
			log.error( e.getMessage(), e );
			Thread.currentThread().interrupt();
		}
	}

	public void load() throws LoaderException {
		try {
			Registry.ConnectivityLock.lockWrite();
			Registry.getContext().getProgressIndicator().setString("Loading events list...");
			if ( !Registry.getContext().getConnectivity().isConnected() ) {
				Registry.getContext().getConnectivity().submitConnectivityRequest(
					new ConnectivityHandler() {
						public void connected() throws InterruptedException {
							Registry.ConnectivityLock.unlockWrite();
							EventsLoader.this.onConnected();
							Registry.getContext().getProgressIndicator().setString("Events list loaded!");
						}

						public void disabled(NetworkDisabledDetails networkDisabledDetails) throws InterruptedException {
							Registry.getContext().getProgressIndicator().setString("Unable to update events list due to" +
									" connectivity issue");
						}
					});
			} else {
				Registry.ConnectivityLock.unlockWrite();
				EventsLoader.this.onConnected();
				Registry.getContext().getProgressIndicator().setString("Loading events list...");
			}
		} catch ( InterruptedException e ) {
			throw new LoaderException( e.getMessage(), e );
		}
	}

}
