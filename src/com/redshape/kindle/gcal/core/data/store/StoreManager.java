package com.redshape.kindle.gcal.core.data.store;

import com.redshape.kindle.gcal.core.event.AppEvent;
import com.redshape.kindle.gcal.core.event.EventDispatcher;
import com.redshape.kindle.gcal.core.event.EventType;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.data.store
 * @date 11/22/11 11:48 AM
 */
public class StoreManager extends EventDispatcher implements IStoreManager {
	private static final Logger log = Logger.getLogger(StoreManager.class);
	private static IStoreManager defaultInstance = new StoreManager();

	public static class Events extends EventType {

		protected Events( String code ) {
			super(code);
		}

		public static final Events Clear = new Events("StoreManager.Events.Clear");
		public static final Events Added = new Events("StoreManager.Events.Added");

	}

	private Map registry = new HashMap();

	public static void setDefault( IStoreManager managerInstance ) {
		defaultInstance = managerInstance;
	}

	public static IStoreManager getDefault() {
		return defaultInstance;
	}

	public void clear() {
		this.registry.clear();
		this.dispatch( new AppEvent( Events.Clear ) );
	}

	public void add(IStore store) {
		this.registry.put( store.getClass(), store );
		this.dispatch( new AppEvent( Events.Added, new Object[] { store }));
	}

	public void addAll(List list) {
		for ( int i = 0; i < list.size(); i++ ) {
			this.add( (IStore) list.get(i) );
		}
	}

	public IStore getStore(Class storeClazz) {
		return getStore(storeClazz, new Object[] {} );
	}

	public IStore getStore(Class storeClazz, Object[] arguments ) {
		IStore store = null;
		if ( arguments.length == 0 && this.registry.containsKey(storeClazz) ) {
			store = (IStore) this.registry.get(storeClazz);
		} else {
			try {
				store = this.createInstance(storeClazz, arguments);

				if ( store != null && arguments.length == 0 ) {
					this.registry.put( storeClazz, store );
				}
			} catch ( Throwable e ) {
				log.error( e.getMessage(), e );
			}
		}

		return store;
	}

	protected IStore createInstance( Class storeClazz, Object[] arguments  ) {
		try {
			Constructor constructor;
			if ( arguments.length == 0 ) {
				constructor = storeClazz.getConstructor( new Class[] {} );
			} else {
				Class[] argTypes = new Class[arguments.length];
				for( int i = 0; i < arguments.length; i++ ) {
					if ( arguments[i] != null ) {
						argTypes[i] = arguments[i].getClass();
					} else {
						argTypes[i] = Object.class;
					}
				}

				constructor = storeClazz.getConstructor( argTypes );
			}

			return  (IStore) constructor.newInstance(arguments);
		} catch ( Throwable e ) {
			log.error(e.getMessage(), e );
			return null;
		}
	}

	public List list() {
		return new ArrayList( Arrays.asList(this.registry.values().toArray()) );
	}
}
