package com.redshape.kindle.gcal.core.data.store.loaders;

import com.redshape.kindle.gcal.core.event.EventType;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.data.store.loaders
 * @date 11/13/11 2:36 PM
 */
public class LoaderEvent extends EventType {

	protected LoaderEvent(String code) {
		super(code);
	}

	public static final LoaderEvent BeforeLoad = new LoaderEvent("Loaders.Events.Before");
	public static final LoaderEvent Loaded = new LoaderEvent("Loaders.Events.Loaded");

	public String toString() {
		return this.code();
	}

	public boolean equals(Object obj) {
		return obj != null && LoaderEvent.class.isAssignableFrom(obj.getClass())
				&& this.code().equals( ( (LoaderEvent) obj ).code() );
	}

	public int hashCode() {
		return this.code().hashCode();
	}
}
