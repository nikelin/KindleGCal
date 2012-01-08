package com.redshape.kindle.gcal.core.data.store;

import com.redshape.kindle.gcal.core.data.store.loaders.IDataLoader;
import com.redshape.kindle.gcal.core.data.store.loaders.LoaderException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.data.store
 * @date 11/13/11 2:38 PM
 */
public class ListStore extends AbstractStore {
	private List items = new ArrayList();

	public ListStore(IDataLoader loader) throws LoaderException {
		super(loader);
	}

	public Object get(int idx) {
		return this.items.get(idx);
	}

	protected List getItems() {
		return this.items;
	}

}
