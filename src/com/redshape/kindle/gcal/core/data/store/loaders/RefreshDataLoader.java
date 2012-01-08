package com.redshape.kindle.gcal.core.data.store.loaders;

import com.amazon.kindle.kindlet.util.TimerTask;
import com.redshape.kindle.gcal.core.data.store.StoreEvent;
import com.redshape.kindle.gcal.core.event.AppEvent;
import com.redshape.kindle.gcal.core.event.EventType;
import com.redshape.kindle.gcal.core.event.IEventListener;
import com.redshape.kindle.gcal.core.utils.Registry;
import com.redshape.kindle.gcal.ui.Dispatcher;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: nikelin
 * Date: 4/24/11
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class RefreshDataLoader implements IDataLoader {
	private static final long serialVersionUID = -1235475449895087975L;
	private static final Logger log = Logger.getLogger( RefreshDataLoader.class );

    private IDataLoader loader;
    private int interval;

    public RefreshDataLoader( IDataLoader loader, int refreshInterval )
    	throws LoaderException {
        this.loader = loader;
        this.interval = refreshInterval;

        this.init();
    }

	public void setAttribute(String name, Object value) {
		this.loader.setAttribute(name, value);
	}

	public Object getAttribute(String name) {
		return this.loader.getAttribute(name);
	}

	public Map getAttributes() {
		return this.loader.getAttributes();
	}

	protected void forceRefresh() {
		Registry.cancelTimer();
		Registry.startTimer();
		this.scheduleInitial();
	}

	protected void scheduleInitial() {
		Registry.getRefreshTimer().scheduleAtFixedRate( new TimerTask() {
            public void run() {
                try {
                    RefreshDataLoader.this.load();
                } catch ( Throwable e ) {
					throw new RuntimeException( e.getMessage(), e );
                }
            }
        }, 0, this.interval );
	}

    protected void init() {
		Dispatcher.get().addListener( StoreEvent.Refresh,
			new IEventListener() {
				public void handleEvent(AppEvent event) {
					RefreshDataLoader.this.interval =
							( (Long) Registry.get( Registry.Attribute.RefreshTime ) ).intValue();

					RefreshDataLoader.this.forceRefresh();
				}
			}
		);

		if ( Registry.get(Registry.Attribute.RefreshEnabled).equals(Boolean.TRUE) ) {
			this.scheduleInitial();
		}
	}

	public void load() throws LoaderException {
		this.loader.load();
	}

	public void addListener(EventType type, IEventListener listener) {
		this.loader.addListener( type, listener );
	}

	public void dispatch(EventType type) {
		this.loader.dispatch( type );
	}

	public void dispatch(EventType type, Object[] args) {
		this.loader.dispatch( type, args );
	}

	public void dispatch(AppEvent event) {
		this.loader.dispatch( event );
	}
}

