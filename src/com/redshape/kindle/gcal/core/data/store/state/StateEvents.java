package com.redshape.kindle.gcal.core.data.store.state;

import com.redshape.kindle.gcal.core.event.EventType;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.data.store.state
 * @date 11/25/11 9:59 AM
 */
public class StateEvents extends EventType {

	protected StateEvents(String code) {
		super(code);
	}

	public static final StateEvents Restore = new StateEvents("StateEvents.Restore");
	public static final StateEvents Save = new StateEvents("StateEvents.Save");
}
