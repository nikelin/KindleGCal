package com.redshape.kindle.gcal.core.data.store.loaders;

import com.redshape.kindle.gcal.core.event.EventDispatcher;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.data.store
 * @date 11/13/11 2:28 PM
 */
public abstract class AbstractDataLoader extends EventDispatcher implements IDataLoader {
	private Map attributes = new HashMap();

	public void setAttribute( String name, Object value ) {
		this.attributes.put(name, value);
	}

	public Map getAttributes() {
		return this.attributes;
	}

	public Object getAttribute( String name ) {
		return this.attributes.get(name);
	}

}
