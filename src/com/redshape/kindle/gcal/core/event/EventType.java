package com.redshape.kindle.gcal.core.event;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.event
 * @date 11/13/11 2:31 PM
 */
public class EventType {
	private String code;

	protected EventType( String code ) {
		this.code = code;
	}

	public String code() {
		return this.code;
	}

}
