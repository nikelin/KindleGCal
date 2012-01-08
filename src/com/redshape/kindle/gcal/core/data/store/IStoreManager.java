package com.redshape.kindle.gcal.core.data.store;

import com.redshape.kindle.gcal.core.event.IEventDispatcher;

import java.util.List;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.data.store
 * @date 11/22/11 11:48 AM
 */
public interface IStoreManager extends IEventDispatcher {

	public void clear();

	public void add( IStore store );

	public void addAll( List list );

	public IStore getStore( Class storeClazz );

	public IStore getStore( Class storeClazz, Object[] args );

	public List list();

}
