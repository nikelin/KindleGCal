package com.redshape.kindle.gcal.core.event;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.event
 * @date 11/13/11 2:30 PM
 */
public interface IEventDispatcher {

	public void addListener( EventType type, IEventListener listener );

	public void dispatch( EventType type );

	public void dispatch( EventType type, Object[] args );

	public void dispatch( AppEvent event );

}
