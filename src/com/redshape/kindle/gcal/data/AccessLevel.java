package com.redshape.kindle.gcal.data;

import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;

public class AccessLevel {
	private String name;
	
	private final static Map REGISTRY = new HashMap();
	
	protected AccessLevel( String name ) {
		this.name = name;
		REGISTRY.put(name, this);
	}
	
	public String name() {
		return this.name;
	}
	
	public static final AccessLevel OWNER = new AccessLevel("owner");
	
	public static Collection values() {
		return REGISTRY.values();
	}
	
	public static AccessLevel valueOf( String name ) {
		Iterator iterator = REGISTRY.entrySet().iterator();
		while( iterator.hasNext() ) {
			Map.Entry entry = (Map.Entry) iterator.next();
			if ( entry.getKey().equals(name) ) {
				return (AccessLevel) entry.getValue();
			}
		}
		
		return null;
	}
	
	public String toString() {
		return this.name();
	}
	
}
