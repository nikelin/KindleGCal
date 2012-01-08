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
import com.redshape.kindle.gcal.ui.Dispatcher;
import com.redshape.kindle.gcal.ui.ViewFacade;
import org.apache.log4j.Logger;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.data.store
 * @date 11/17/11 2:00 PM
 */
public class CalendarsLoader extends AbstractDataLoader implements IEventListener {
	private static final Logger log = Logger.getLogger( CalendarsLoader.class );

	public CalendarsLoader() {
		super();

		this.init();
	}

	protected void init() {
		Dispatcher.get().addListener(GoogleCalendarService.Events.Calendars, this);
		Dispatcher.get().addListener( GoogleCalendarService.Events.CalendarsLoadingFail, this );
	}

	public void load() throws LoaderException {
		try {
			Registry.ConnectivityLock.lockWrite();
			Registry.getContext().getProgressIndicator().setString("Loading calendars list...");
			if ( !Registry.getContext().getConnectivity().isConnected() ) {
				Registry.getContext().getConnectivity().submitConnectivityRequest(
					new ConnectivityHandler() {
						public void connected() throws InterruptedException {
							Registry.ConnectivityLock.unlockWrite();
							CalendarsLoader.this.onConnected();
							Registry.getContext().getProgressIndicator().setString("Calendars list loaded...");
						}

						public void disabled(NetworkDisabledDetails networkDisabledDetails) throws InterruptedException {
							Registry.getContext().getProgressIndicator().setString("Unable to update calendars list due" +
									" to connectivity issue!");
						}
					}
				);
			} else {
				Registry.ConnectivityLock.unlockWrite();
				this.onConnected();
				Registry.getContext().getProgressIndicator().setString("Calendars list loaded...");
			}
		} catch ( InterruptedException e ) {
			throw new LoaderException( e.getMessage(), e );
		}
	}

	protected void onConnected() {
		try {
			Registry.getService().retrieveCalendars();
		} catch ( Throwable e ) {
			ViewFacade.showError("Sync failed", "Calendars synchronization failed!");
			log.error(e.getMessage(), e);
		}
	}

	public void handleEvent(AppEvent event) {
		if ( event.getType().equals( GoogleCalendarService.Events.Calendars ) ) {
			this.dispatch( LoaderEvent.BeforeLoad );
			this.dispatch(
				new AppEvent( LoaderEvent.Loaded, new Object[] { event.getArg(0) } )
			);
		} else if ( event.getType().equals( GoogleCalendarService.Events.CalendarsLoadingFail ) ) {
			ViewFacade.showError("Sync failed", "Calendars synchronization failed!");
		}
	}
}
