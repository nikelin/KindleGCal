package com.redshape.kindle.gcal.core.data.store;

import com.redshape.kindle.gcal.core.data.store.loaders.LoaderException;
import com.redshape.kindle.gcal.core.event.AppEvent;
import com.redshape.kindle.gcal.core.event.EventType;
import com.redshape.kindle.gcal.core.event.IEventListener;
import com.redshape.kindle.gcal.core.utils.IFilter;

import java.util.List;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.data.store
 * @date 11/30/11 4:19 PM
 */
public class StoreProjection implements IStore {

	private IStore origin;
	private IStore filtered;
	private boolean initialized;
	private IFilter filter;

	public StoreProjection( IStore store, IFilter filter ) throws LoaderException {
		super();

		if ( store == null || filter == null ) {
			throw new IllegalArgumentException("<null>");
		}

		this.origin = store;
		this.filter = filter;

		this.init();
	}

	protected IFilter getFilter() {
		return this.filter;
	}

	public IStore getOrigin() {
		return this.origin;
	}

	public void init() throws LoaderException {
		this.refresh();
	}

	public boolean isEmpty() {
		return this.filtered.isEmpty();
	}

	public int count() {
		return this.filtered.count();
	}

	public void addAll(List items) {
		throw new IllegalArgumentException("Read-only operations supported");
	}

	public void add(Object serializable) {
		throw new IllegalArgumentException("Read-only operations supported");
	}

	public void remove(Object serializable) {
		throw new IllegalArgumentException("Read-only operations supported");
	}

	public Object get(int idx) {
		return this.filtered.get(idx);
	}

	public List list() {
		return this.filtered.list();
	}

	public void clear() {
		throw new IllegalArgumentException("Read-only operations supported");
	}

	public void refresh() throws LoaderException {
		if ( this.filtered == null ) {
			this.filtered = new ListStore(null);
		}

		this.filtered.clear();
		for ( int i = 0; i < this.getOrigin().count(); i++ ) {
			Object record = this.getOrigin().get(i);
			if ( this.getFilter().filter( record ) ) {
				this.filtered.add( record );
			}
		}
	}

	public void addListener(EventType type, IEventListener listener) {
		this.filtered.addListener( type, listener );
	}

	public void dispatch(EventType type) {
		this.filtered.dispatch( type );
	}

	public void dispatch(EventType type, Object[] args) {
		this.filtered.dispatch( type, args );
	}

	public void dispatch(AppEvent event) {
		this.filtered.dispatch( event );
	}
}
