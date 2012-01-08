package com.redshape.kindle.gcal.core.net;

import com.amazon.kindle.kindlet.KindletContext;
import com.redshape.kindle.gcal.core.event.AppEvent;
import com.redshape.kindle.gcal.core.event.IEventListener;
import com.redshape.kindle.gcal.core.net.auth.AuthenticatorException;
import com.redshape.kindle.gcal.core.net.auth.ClientLogin;
import com.redshape.kindle.gcal.core.net.auth.IAuthenticator;
import com.redshape.kindle.gcal.core.net.http.HttpRequest;
import com.redshape.kindle.gcal.ui.ViewFacade;

import java.io.IOException;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.net
 * @date 11/17/11 3:36 PM
 */
public class AuthenticatedConnector extends Connector {
	private IAuthenticator authenticator;

	public AuthenticatedConnector(KindletContext context, IAuthenticator authenticator) {
		super(context);

		this.authenticator = authenticator;
		this.init();
	}

	protected void init() {
		this.authenticator.addListener(
			ClientLogin.Events.Complete,
			new IEventListener() {
				public void handleEvent(AppEvent event) {
					try {
						if ( event.getArgs().length < 2
								|| event.getArg(1) == null ) {
							AuthenticatedConnector.this.request(( HttpRequest ) event.getArg(0));
						} else {
							AuthenticatedConnector.super.request( (HttpRequest) event.getArg(0),
									(IEventListener) event.getArg(1) );
						}
					} catch ( Throwable e ) {
						ViewFacade.showError("I/O exception", e.getMessage() );
						throw new RuntimeException(e);
					}
				}
			}
		);
	}

	public IAuthenticator getAuthenticator() {
		return authenticator;
	}

	public void request( final HttpRequest request,
						 final IEventListener listener ) throws IOException {
		try {
			this.getAuthenticator().authorize(request, new IEventListener() {
				public void handleEvent( AppEvent event ) {
					try {
						AuthenticatedConnector.super.request(request, listener);
					} catch ( Throwable e ) {
						ViewFacade.showError( "I/O exception", e.getMessage() );
						throw new RuntimeException( e );
					}
				}
			});
		} catch ( AuthenticatorException e ) {
			throw new IOException( e.getMessage(), e );
		}
	}

}
