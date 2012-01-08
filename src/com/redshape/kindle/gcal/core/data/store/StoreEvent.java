package com.redshape.kindle.gcal.core.data.store;

import com.redshape.kindle.gcal.core.event.EventType;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.data.store
 * @date 11/13/11 2:43 PM
 */
public class StoreEvent extends EventType {

	protected StoreEvent(String code) {
		super(code);
	}

	public static final StoreEvent Loaded = new StoreEvent("Store.Events.Loaded");
	public static final StoreEvent BeforeAdded = new StoreEvent("Store.Events.BeforeAdded");
	public static final StoreEvent Added = new StoreEvent("Store.Events.Added");
	public static final StoreEvent BeforeRemove = new StoreEvent("Store.Events.BeforeRemove");
	public static final StoreEvent Removed = new StoreEvent("Store.Events.Removed");
	public static final StoreEvent Refresh = new StoreEvent("Store.Events.Refresh");

}
