package com.redshape.kindle.gcal.core.data.store.loaders;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.data.store.loaders
 * @date 11/20/11 1:41 PM
 */
public class LoaderException extends Exception {

	public LoaderException(String message) {
		this(message, null);
	}

	public LoaderException(String message, Throwable cause) {
		super(message, cause);
	}

}
