package com.redshape.kindle.gcal.core.net.auth;

import com.redshape.kindle.gcal.core.event.IEventDispatcher;
import com.redshape.kindle.gcal.core.event.IEventListener;
import com.redshape.kindle.gcal.core.net.http.HttpRequest;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.service
 * @date 11/17/11 2:20 PM
 */
public interface IAuthenticator extends IEventDispatcher {

	public void authorize( HttpRequest request ) throws AuthenticatorException;

	public void authorize( HttpRequest request, IEventListener listener )  throws AuthenticatorException;

}
