package com.redshape.kindle.gcal.core.net;

import com.redshape.kindle.gcal.core.net.http.HttpRequest;

import java.io.IOException;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.service
 * @date 11/17/11 2:20 PM
 */
public interface IConnector {

	public void request( HttpRequest request ) throws IOException;

}
