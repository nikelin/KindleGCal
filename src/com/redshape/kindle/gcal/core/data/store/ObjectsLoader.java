package com.redshape.kindle.gcal.core.data.store;

import java.io.*;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.data.store
 * @date 11/25/11 9:05 AM
 */
public class ObjectsLoader {

	public static void flush( Object source, OutputStream target ) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(10000);
		ObjectOutputStream out = new ObjectOutputStream(bos);
		out.writeObject( source );
		out.flush();
		out.close();

		target.write( bos.toByteArray() );
	}

	public static Object load( InputStream source) throws IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(source);
		Object result = (Object) in.readObject();
		in.close();

		return result;
	}

}
