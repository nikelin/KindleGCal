package com.redshape.kindle.gcal.core.data.store.state;

import com.redshape.kindle.gcal.core.data.store.IStore;
import com.redshape.kindle.gcal.core.data.store.ObjectsLoader;
import com.redshape.kindle.gcal.core.data.store.StoreManager;
import com.redshape.kindle.gcal.core.event.EventDispatcher;
import com.redshape.kindle.gcal.core.utils.Registry;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.data.store.state
 * @date 11/25/11 8:57 AM
 */
public class StateManager extends EventDispatcher implements IStateManager {
	private static final Logger log = Logger.getLogger( StateManager.class );
	private static final String __STORES_STATE_FILE_NAME = ".gcal.stores.state.bin";
	private static final String __REGISTRY_STATE_FILE_NAME = ".gcal.registry.state.bin";

	private static StateManager defaultInstance = new StateManager();

	public static StateManager getDefault() {
		return defaultInstance;
	}

	public static void setDefault( StateManager manager ) {
		defaultInstance = manager;
	}

	public void save() throws IOException {
		this.saveStores();
		this.saveRegistry();
		this.dispatch(StateEvents.Save);
	}

	protected void saveStores() throws IOException {
		ObjectsLoader.flush(
			StoreManager.getDefault().list(),
			this.openStoresTargetStream()
		);
	}

	protected void saveRegistry() throws IOException {
		Map map = Registry.attributesMap();
		Map dump = new HashMap();
		Iterator entriesIterator = map.entrySet().iterator();
		while ( entriesIterator.hasNext() ) {
			Map.Entry entry = (Map.Entry) entriesIterator.next();
			Registry.Attribute key = (Registry.Attribute) entry.getKey();
			if ( !key.isRestorable() ) {
				continue;
			}

			dump.put( key, entry.getValue() );
		}

		ObjectsLoader.flush( dump, this.openRegistryTargetStream() );
	}

	public void restore() throws IOException {
		this.restoreStores();
		this.restoreRegistry();
		this.dispatch( StateEvents.Restore );
	}

	protected void restoreRegistry() throws IOException {
		File storeFile = this.getRegistryStore();
		if ( !storeFile.exists() || storeFile.length() == 0 ) {
			return;
		}

		log.info("Restore file path: " + storeFile.getAbsolutePath() );

		try {
			Map state = (Map) ObjectsLoader.load( new FileInputStream( storeFile ) );

			Iterator entriesIterator = state.entrySet().iterator();
			while ( entriesIterator.hasNext() ) {
				Map.Entry stateEntry = (Map.Entry) entriesIterator.next();

				Registry.Attribute key = (Registry.Attribute) stateEntry.getKey();
				if ( key.isRestorable() ) {
					Registry.set(
						(Registry.Attribute) stateEntry.getKey(),
						stateEntry.getValue()
					);
				}
			}
		} catch ( Throwable e ) {
			try {
				if ( storeFile.exists() ) {
					storeFile.delete();
				}
			} catch ( Throwable ex ) {}

			throw new IOException( e.getMessage(), e );
		}
	}

	protected void restoreStores() throws IOException {
		File stateStore = this.getStateStore();
		try {
			if ( !stateStore.exists() && stateStore.length() == 0 ) {
				return;
			}

			log.info("Restore file path: " + stateStore.getAbsolutePath() );

			StoreManager.getDefault().clear();

			List stores = ( List ) ObjectsLoader.load(new FileInputStream(stateStore));
			for ( int i = 0; i < stores.size(); i++ ) {
				IStore store = (IStore) stores.get(i);
				store.reinitialize();
				StoreManager.getDefault().add(store);
			}
		} catch ( Throwable e ) {
			try {
				if ( stateStore.exists() ) {
					stateStore.delete();
				}
			} catch ( Throwable ex ) {}

			throw new IOException( e.getMessage(), e );
		}
	}

	protected File getRegistryStore() throws IOException {
		return new File( Registry.getContext().getHomeDirectory(), __REGISTRY_STATE_FILE_NAME );
	}

	protected File getStateStore() throws IOException {
		return new File( Registry.getContext().getHomeDirectory(), __STORES_STATE_FILE_NAME );
	}

	protected OutputStream openRegistryTargetStream() throws IOException {
		return new FileOutputStream( this.getRegistryStore(), false);
	}

	protected OutputStream openStoresTargetStream() throws IOException {
		return new FileOutputStream( this.getStateStore(), false);
	}

}
