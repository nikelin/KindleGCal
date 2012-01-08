package com.redshape.kindle.gcal.ui;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KPanel;

public abstract class ContextAwareView extends KPanel {
	private KindletContext context;
	
	public ContextAwareView( KindletContext context ) {
		this.context = context;

		this.buildUI();
		this.configUI();
	}

	abstract public void activate();

	abstract protected void buildUI();

	abstract protected void configUI();
	
	protected KindletContext getContext() {
		return this.context;
	}

}
