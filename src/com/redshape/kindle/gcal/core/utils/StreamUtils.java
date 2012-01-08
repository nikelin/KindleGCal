package com.redshape.kindle.gcal.core.utils;

import java.io.*;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.utils
 * @date 11/17/11 4:18 PM
 */
public final class StreamUtils {

	public static String readToString( InputStream stream ) throws IOException {
		BufferedReader reader = new BufferedReader( new InputStreamReader(stream) );

		String tmp;
		StringBuffer builder = new StringBuffer();
		while ( null != ( tmp = reader.readLine() ) ) {
			builder.append(tmp)
				.append("\n");
		}

		return builder.toString();
	}

	public static void copyStreams( InputStream input, OutputStream output ) {
		//@TODO
	}

}
