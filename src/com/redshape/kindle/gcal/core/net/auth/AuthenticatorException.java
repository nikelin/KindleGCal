package com.redshape.kindle.gcal.core.net.auth;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.net.auth
 * @date 11/17/11 3:41 PM
 */
public class AuthenticatorException extends Exception {

	public AuthenticatorException() {
		this(null);
	}

	public AuthenticatorException( String message ) {
		this(message, null);
	}

	public AuthenticatorException(String message, Throwable cause) {
		super(message, cause);
	}

}
