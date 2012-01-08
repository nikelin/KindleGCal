package com.redshape.kindle.gcal.ui;

import com.redshape.kindle.gcal.core.event.EventDispatcher;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.ui
 * @date 11/30/11 12:02 AM
 */
public class Dispatcher extends EventDispatcher {
	private static final Dispatcher dispatcher = new Dispatcher();

	public static Dispatcher get() {
		return dispatcher;
	}

}
