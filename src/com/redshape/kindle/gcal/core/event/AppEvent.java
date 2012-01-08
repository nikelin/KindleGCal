package com.redshape.kindle.gcal.core.event;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.event
 * @date 11/13/11 2:31 PM
 */
public class AppEvent {

	private Object[] args;
	private EventType type;

	public AppEvent( EventType type ) {
		this(type, new Object[] {} );
	}

	public AppEvent( EventType type, Object[] args ) {
		this.type = type;
		this.args = args;
	}

	public EventType getType() {
		return this.type;
	}

	public Object getArg( int idx ) {
		return this.args[idx];
	}

	public Object[] getArgs() {
		return this.args;
	}

}
