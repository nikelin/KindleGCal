package com.redshape.kindle.gcal.data;

import java.io.Serializable;

public class Author implements Serializable {
	
	private String email;
	private String displayName;

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
