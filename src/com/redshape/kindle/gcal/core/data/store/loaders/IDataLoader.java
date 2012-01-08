package com.redshape.kindle.gcal.core.data.store.loaders;

import com.redshape.kindle.gcal.core.event.IEventDispatcher;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.data.store
 * @date 11/13/11 2:27 PM
 */
public interface IDataLoader extends IEventDispatcher, Serializable {

	public void setAttribute( String name, Object value );

	public Object getAttribute( String name );

	public Map getAttributes();

	public void load() throws LoaderException;

}
