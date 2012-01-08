package com.redshape.kindle.gcal.ui.views;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KLabelMultiline;
import com.redshape.kindle.gcal.ui.ContextAwareView;

import java.util.ResourceBundle;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.ui.views
 * @date 11/13/11 12:43 PM
 */
public class AboutView extends ContextAwareView {
	private ResourceBundle bundle = ResourceBundle.getBundle("AppKindlet");

	public AboutView(KindletContext context) {
		super(context);
	}

	protected void buildUI() {
		KLabelMultiline multilineLabel = new KLabelMultiline();
		multilineLabel.setText( bundle.getString("AppKindlet.About.Text") );
		this.add(multilineLabel);
	}

	public void activate() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	protected void configUI() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

}
