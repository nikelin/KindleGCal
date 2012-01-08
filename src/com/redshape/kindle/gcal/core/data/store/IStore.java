package com.redshape.kindle.gcal.core.data.store;

import com.redshape.kindle.gcal.core.data.store.loaders.LoaderException;
import com.redshape.kindle.gcal.core.event.IEventDispatcher;

import java.io.Serializable;
import java.util.List;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.data
 * @date 11/13/11 2:26 PM
 */
public interface IStore extends IEventDispatcher, Serializable {

	public void reinitialize() throws LoaderException;

	public void init() throws LoaderException;

	public boolean isEmpty();

	public int count();

	public Object get( int idx );

	public void addAll( List items );

	public void add( Object serializable );

	public void remove( Object serializable );

	public List list();

	public void clear();

	public void refresh() throws LoaderException;

}
