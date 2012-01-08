package com.redshape.kindle.gcal.core.data.store;

import com.redshape.kindle.gcal.core.data.store.loaders.IDataLoader;
import com.redshape.kindle.gcal.core.data.store.loaders.LoaderEvent;
import com.redshape.kindle.gcal.core.data.store.loaders.LoaderException;
import com.redshape.kindle.gcal.core.event.AppEvent;
import com.redshape.kindle.gcal.core.event.EventDispatcher;
import com.redshape.kindle.gcal.core.event.IEventListener;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.data.store
 * @date 11/13/11 2:38 PM
 */
public abstract class AbstractStore extends EventDispatcher implements IStore, IEventListener, Serializable {
	private boolean initialized;
	private IDataLoader loader;

	AbstractStore( IDataLoader loader ) throws LoaderException{
		this.loader = loader;
	}

	abstract protected List getItems();

	public void handleEvent(AppEvent event) {
		if ( event.getType().equals( LoaderEvent.Loaded ) ) {
			this.addAll( (List) event.getArg(0) );
		}
	}

	public void addAll( List items ) {
		Iterator itemsIterator = items.iterator();
		while( itemsIterator.hasNext() ) {
			this.add( itemsIterator.next() );
		}
	}

	public void reinitialize() throws LoaderException {
		this.initialized = false;
		this.init();
		this.refresh();
	}

	public synchronized void init() throws LoaderException {
		if ( this.initialized ) {
			return;
		}

		if ( this.loader != null ) {
			this.loader.addListener(LoaderEvent.Loaded, this);
			this.loader.load();
		}

		this.initialized = true;
	}

	public boolean isEmpty() {
		return this.getItems().isEmpty();
	}

	public int count() {
		return this.getItems().size();
	}

	public void add(Object serializable) {
		this.dispatch( StoreEvent.BeforeAdded, new Object[] { serializable } );
		this.getItems().add(serializable);
		this.dispatch( StoreEvent.Added, new Object[] { serializable } );
	}

	public void remove(Object serializable) {
		this.dispatch( StoreEvent.BeforeRemove, new Object[] { serializable } );
		this.getItems().remove(serializable);
		this.dispatch( StoreEvent.Removed, new Object[] { serializable } );
	}

	public List list() {
		return this.getItems();
	}

	public void clear() {
		for ( int i = 0; i < this.count(); i++ ) {
			this.remove( this.get(i) );
		}

		this.getItems().clear();
	}

	public void refresh() throws LoaderException {
		this.dispatch( StoreEvent.Refresh );

		if ( this.loader != null ) {
			this.clear();
			this.loader.load();
		}
	}

}
