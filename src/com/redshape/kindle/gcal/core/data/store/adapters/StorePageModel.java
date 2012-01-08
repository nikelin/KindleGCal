package com.redshape.kindle.gcal.core.data.store.adapters;

import com.amazon.kindle.kindlet.ui.pages.LocationIterator;
import com.amazon.kindle.kindlet.ui.pages.LocationIterators;
import com.amazon.kindle.kindlet.ui.pages.PageModel;
import com.redshape.kindle.gcal.core.data.store.IStore;

import java.util.NoSuchElementException;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.data.store.adapters
 * @date 11/30/11 9:22 PM
 */
public class StorePageModel implements PageModel {
	private IStore store;
	private int perPage;

	public StorePageModel( IStore store ) {
		this(store, 25);
	}

	public StorePageModel( IStore store, int perPage ) {
		if ( store == null ) {
			throw new IllegalArgumentException("<null>");
		}

		this.perPage = perPage;
		this.store = store;
	}

	public int getInitialLocation() {
		return 0;
	}

	public int getFirstLocation() {
		return 0;
	}

	public int getLastLocation() {
		return this.store.count();
	}

	public LocationIterator locationIterator(int i, boolean b) {
		return LocationIterators.locationIterator( this.store.list().subList(
				i * this.perPage, this.perPage ), i, b );
	}

	public Object getElementAt(int i) throws NoSuchElementException {
		return this.store.get(i);
	}
}
