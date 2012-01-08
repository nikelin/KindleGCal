package com.redshape.kindle.gcal.ui.views;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.*;
import com.amazon.kindle.kindlet.ui.pages.LocationIterator;
import com.amazon.kindle.kindlet.ui.pages.PageProvider;
import com.redshape.kindle.gcal.core.data.store.IStore;
import com.redshape.kindle.gcal.core.data.store.StoreEvent;
import com.redshape.kindle.gcal.core.data.store.StoreManager;
import com.redshape.kindle.gcal.core.data.store.StoreProjection;
import com.redshape.kindle.gcal.core.data.store.adapters.StorePageModel;
import com.redshape.kindle.gcal.core.data.store.loaders.LoaderException;
import com.redshape.kindle.gcal.core.event.AppEvent;
import com.redshape.kindle.gcal.core.event.EventType;
import com.redshape.kindle.gcal.core.event.IEventListener;
import com.redshape.kindle.gcal.core.net.service.GoogleCalendarService;
import com.redshape.kindle.gcal.core.utils.Constants;
import com.redshape.kindle.gcal.core.utils.IFilter;
import com.redshape.kindle.gcal.data.Calendar;
import com.redshape.kindle.gcal.data.Entry;
import com.redshape.kindle.gcal.data.stores.EventsStore;
import com.redshape.kindle.gcal.ui.ContextAwareView;
import com.redshape.kindle.gcal.ui.Dispatcher;
import com.redshape.kindle.gcal.ui.ViewFacade;
import org.apache.log4j.Logger;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.ui.views
 * @date 11/30/11 1:09 AM
 */
public class EventsView extends ContextAwareView implements IEventListener {
	private static final Logger log = Logger.getLogger( EventsView.class );
	private Date currentDate = new Date();

	public static class Events extends EventType {

		protected Events( String code ) {
			super(code);
		}

		public static final Events Goto = new Events("EventsView.Events.Goto");
		public static final Events NextDay = new Events("EventsView.Events.NextDay");
		public static final Events PreviousDay = new Events("EventsView.Events.PreviousDay");

	}

	public static class KBoxEntryProvider implements PageProvider {

		public void setPageSize(Dimension dimension) {
		}

		public Component getPage(LocationIterator locationIterator) {
			KPanel box = new KPanel();
			box.setLayout( new GridLayout(0, 2, 5, 20) );
			while ( locationIterator.hasNext() ) {
				Entry object = (Entry) locationIterator.next();
				box.add( new KLabelMultiline( object.getSummary()  ));
			}

			return box;
		}

		public int getPageStartLocation() {
			return 0;
		}

		public int getPageEndLocation() {
			return 20;
		}
	}

	private DateFormat dateFormat = new SimpleDateFormat("dd'th' E 'of' M");
	private DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");

	private IStore store;
	private Calendar calendar;
	private KBox box;
	private KPagedContainer container;

	public EventsView(KindletContext context, Calendar calendar ) {
		super(context);

		this.calendar = calendar;

		this.init();
		this.bindEvents();
	}

	protected void bindEvents() {
		Dispatcher.get().addListener( Events.NextDay, this );
		Dispatcher.get().addListener( Events.PreviousDay, this );
	}

	protected void bindStore( IStore store ) {
		store.addListener(StoreEvent.Loaded, this);
		store.addListener(StoreEvent.Added, this);
		store.addListener(StoreEvent.Removed, this);
	}

	protected void init() {
		this.add( new KLabel( String.format("%s",
				new Object[] { this.dateFormat.format( this.currentDate ) } ) ) );

		this.store = StoreManager.getDefault().getStore(EventsStore.class, new Object[] { calendar } );
		this.bindStore(this.store);

		this.container = new KPagedContainer( new StorePageModel( this.store ), new KBoxEntryProvider() );
		this.add( this.container );

		try {
			this.store.init();
		} catch ( LoaderException e ) {
			log.error( e.getMessage(), e );
			ViewFacade.showError("Error", e.getMessage());
		}
	}

	public void activate() {

	}

	protected void buildUI() {
		this.setLayout(new KBoxLayout(this, KBoxLayout.Y_AXIS));
	}

	public void handleEvent(AppEvent event) {
		if ( event.getType().equals( StoreEvent.Added ) ) {
			this.container.invalidate();
			this.container.relayoutPage();
			this.container.repaint();
		} else if ( event.getType().equals( StoreEvent.Removed ) ) {
			this.container.invalidate();
			this.container.relayoutPage();
			this.container.repaint();
		} else if ( event.getType().equals( StoreEvent.Refresh ) ) {
			this.onClear();
		} else if ( event.getType().equals( Events.PreviousDay ) ) {
			this.container.previous();
		} else if ( event.getType().equals( Events.NextDay  ) ) {
			this.container.next();
		} else if ( event.getType().equals( Events.Goto ) ) {
			this.onDateShift( (Date) event.getArg(0) );
		} else if ( event.getType().equals( StoreEvent.Loaded ) ) {
			this.onLoaded( (List) event.getArg(0) );
		} else if ( event.getType().equals(GoogleCalendarService.Events.Events) ) {
			this.onLoaded( this.store.list() );
		}
	}

	protected void onLoaded( List results ) {
		if ( !results.isEmpty() ) {
			return;
		}

//		this.box.removeAll();
//		this.box.add( new KLabel("No events on selected day.") );
//		this.box.invalidate();
//		this.box.repaint();
	}

	protected void onDateShift( final Date date ) {
		try {
			IStore origin = this.store;
			while ( origin instanceof StoreProjection ) {
				origin = ( (StoreProjection) origin ).getOrigin();
			}

			IStore store = new StoreProjection( origin, new IFilter() {
				public boolean filter( Object filterable) {
					Entry object = (Entry) filterable;

					int diff = (int) ( date.getTime() - object.getEndDate().getTime() );

					return Math.abs(diff) <= Constants.TIME_DAY;
				}
			});

			this.store = store;
			this.bindStore(store);
			this.store.refresh();
		} catch ( LoaderException e ) {
			ViewFacade.showError( "Error!", e.getMessage() );
		}
	}

	protected void onClear() {
		this.box.removeAll();
	}

	protected void configUI() {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
