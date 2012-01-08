package com.redshape.kindle.gcal.ui.panels;

import com.amazon.kindle.kindlet.ui.KTextOptionListMenu;
import com.amazon.kindle.kindlet.ui.KTextOptionMenuItem;
import com.amazon.kindle.kindlet.ui.KTextOptionPane;
import com.redshape.kindle.gcal.core.data.store.StoreEvent;
import com.redshape.kindle.gcal.core.event.AppEvent;
import com.redshape.kindle.gcal.core.utils.Registry;
import com.redshape.kindle.gcal.ui.Dispatcher;
import org.apache.log4j.Logger;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.ui.panels
 * @date 11/13/11 12:32 PM
 */
public class RefreshTimePane extends KTextOptionPane {
	private static final Logger log = Logger.getLogger( RefreshTimePane.class );

	private static final Map refreshTimeOptions = new LinkedHashMap();
	static {
		refreshTimeOptions.put(new Integer(0), "Refresh now");
		refreshTimeOptions.put(new Integer(5), "5 minutes");
		refreshTimeOptions.put(new Integer(10), "10 minutes");
		refreshTimeOptions.put(new Integer(25), "25 minutes");
	}

	public RefreshTimePane() {
		this.init();
	}

	protected void init() {
		this.addListMenu( this.createOptionsMenu() );
	}

	protected KTextOptionListMenu createOptionsMenu() {
		KTextOptionListMenu menu = new KTextOptionListMenu("Refresh time");
		menu.addItemListener(
			new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					Iterator entriesIterator = refreshTimeOptions.entrySet().iterator();
					while ( entriesIterator.hasNext() ) {
						Map.Entry entry = (Map.Entry) entriesIterator.next();
						if ( !entry.getValue().equals( e.getItem() ) ) {
							continue;
						}


						Integer interval = Integer.valueOf(
							( (Integer) entry.getKey() ).intValue() * 60 * 1000 );
						log.info("New refresh interval: " + interval );

						Registry.set( Registry.Attribute.RefreshTime, new Long( interval.longValue() ) );
						Dispatcher.get().dispatch( new AppEvent(StoreEvent.Refresh) );

						break;
					}
				}
			}
		);

		Set keySet = refreshTimeOptions.keySet();
		Iterator keysIterator = keySet.iterator();
		while ( keysIterator.hasNext() ) {
			Object key = keysIterator.next();
			KTextOptionMenuItem item = new KTextOptionMenuItem(
					refreshTimeOptions.get(key) );
			menu.add( item );
		}

		return menu;
	}

}
