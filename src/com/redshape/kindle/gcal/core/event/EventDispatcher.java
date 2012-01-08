package com.redshape.kindle.gcal.core.event;

import org.apache.log4j.Logger;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.event
 * @date 11/13/11 2:30 PM
 */
public class EventDispatcher implements IEventDispatcher {
	private static final Logger log = Logger.getLogger(EventDispatcher.class);

	private Map events = new HashMap();

	public void addListener(EventType type, IEventListener listener) {
		List listeners = (List) this.events.get(type);
		if ( listeners == null ) {
			this.events.put( type, listeners = new ArrayList() );
		}

		listeners.add( listener );
	}

	public void dispatch(EventType type) {
		this.dispatch( new AppEvent(type) );
	}

	public void dispatch(EventType type, Object[] args) {
		this.dispatch( new AppEvent(type, args) );
	}

	public void dispatch( final AppEvent event) {
		if ( event == null ) {
			throw new IllegalArgumentException("<null>");
		}

		if ( !this.events.containsKey(event.getType()) ) {
			return;
		}

		Iterator listeners = ( (List) this.events.get(event.getType()) )
									.listIterator();
		while ( listeners.hasNext() ) {
			final IEventListener listener = (IEventListener) listeners.next();

			try {
				EventQueue.invokeLater(
					new Runnable() {
						public void run() {
							listener.handleEvent(event);
						}
					}
				);
			} catch ( Throwable e ) {
				log.error( e.getMessage(), e );
			}
		}
	}
}
