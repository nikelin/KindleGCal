package com.redshape.kindle.gcal.core.data.store.state;

import com.redshape.kindle.gcal.core.event.IEventDispatcher;

import java.io.IOException;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.data.store.state
 * @date 11/25/11 8:57 AM
 */
public interface IStateManager extends IEventDispatcher {

	public void save() throws IOException;

	public void restore() throws IOException;

}
