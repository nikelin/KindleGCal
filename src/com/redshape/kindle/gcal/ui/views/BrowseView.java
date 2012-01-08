package com.redshape.kindle.gcal.ui.views;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KBox;
import com.amazon.kindle.kindlet.ui.KBoxLayout;
import com.amazon.kindle.kindlet.ui.KButton;
import com.amazon.kindle.kindlet.ui.KLabel;
import com.redshape.kindle.gcal.core.data.store.IStore;
import com.redshape.kindle.gcal.core.data.store.StoreEvent;
import com.redshape.kindle.gcal.core.data.store.StoreManager;
import com.redshape.kindle.gcal.core.data.store.loaders.LoaderException;
import com.redshape.kindle.gcal.core.event.AppEvent;
import com.redshape.kindle.gcal.core.event.IEventListener;
import com.redshape.kindle.gcal.core.net.service.GoogleCalendarService;
import com.redshape.kindle.gcal.data.Calendar;
import com.redshape.kindle.gcal.data.stores.CalendarStore;
import com.redshape.kindle.gcal.ui.ContextAwareView;
import com.redshape.kindle.gcal.ui.Dispatcher;
import com.redshape.kindle.gcal.ui.ViewFacade;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.ui.views
 * @date 11/13/11 12:36 PM
 */
public class BrowseView extends ContextAwareView implements IEventListener {
	private static final Logger log = Logger.getLogger( BrowseView.class );

	private KBox box;
	private IStore store;

	public BrowseView(KindletContext context) {
		super(context);
	}

	protected void buildUI() {
		this.setLayout(new KBoxLayout(this, KBoxLayout.Y_AXIS));

		this.add( new KLabel("Calendar Events") );
		this.add( this.box = KBox.createVerticalBox() );

		Dispatcher.get().addListener(GoogleCalendarService.Events.Calendars, this);

		try {
			this.store = StoreManager.getDefault().getStore(CalendarStore.class);
			this.store.addListener( StoreEvent.Added, this );
			this.store.addListener( StoreEvent.Removed, this );
			this.store.addListener( StoreEvent.Refresh, this );
			this.store.init();
		} catch ( LoaderException e ) {
			log.error( e.getMessage(), e );
		}
	}

	public void handleEvent(AppEvent event) {
		if ( event.getType().equals(StoreEvent.Added) ) {
			this.onAdded( (Calendar) event.getArg(0) );
		} else if ( event.getType().equals(StoreEvent.Removed) ) {
			this.onRemoved( (Calendar) event.getArg(0));
		} else if ( event.getType().equals(StoreEvent.Refresh) ) {
			this.onRefresh();
		} else if ( event.getType().equals(GoogleCalendarService.Events.Calendars) ) {
			this.onRefresh();
		}
	}

	protected Component createRecordUI( final Calendar record ) {
		KButton button = new KButton( record.getSummary() );
		button.setName( record.getEtag() );
		button.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ViewFacade.showEventsView( record );
			}
		});

		return button;
	}

	protected void onRefresh() {
		this.box.removeAll();
		this.box.invalidate();
		this.box.repaint();
	}

	protected void onAdded( final Calendar record ) {
		if ( !EventQueue.isDispatchThread() ) {
			try {
				EventQueue.invokeAndWait(
					new Runnable() {
						public void run() {
							BrowseView.this.box.add(
								BrowseView.this.createRecordUI(record));
						}
					}
				);
			} catch ( Throwable e ) {
				log.error( e.getMessage(), e );
			}
		} else {
			this.box.add(this.createRecordUI(record));
		}

		this.box.invalidate();
		this.box.repaint();

		ViewFacade.refresh();
	}

	protected void onRemoved( Calendar record ) {
		for ( int componentIdx = 0; componentIdx < this.box.getComponentCount(); componentIdx++ ) {
			Component component = this.box.getComponent(componentIdx);
			if ( component.getName().equals( record.getEtag() ) ) {
				this.box.remove( component );
			}
		}

		this.box.invalidate();
		this.box.repaint();
	}

	public void activate() {
//		try {
//			this.store.refresh();
//		} catch ( Throwable e ) {
//			log.error( e.getMessage(), e );
//			ViewFacade.showError("Error", e.getMessage() );
//		}
	}

	protected void configUI() {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
