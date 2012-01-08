package com.redshape.kindle.gcal.ui.views;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KBox;
import com.amazon.kindle.kindlet.ui.KBoxLayout;
import com.amazon.kindle.kindlet.ui.KButton;
import com.amazon.kindle.kindlet.ui.KTextField;
import com.redshape.kindle.gcal.core.event.AppEvent;
import com.redshape.kindle.gcal.core.event.EventType;
import com.redshape.kindle.gcal.core.event.IEventListener;
import com.redshape.kindle.gcal.ui.ContextAwareView;
import com.redshape.kindle.gcal.ui.ViewFacade;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.ui.views
 * @date 11/20/11 12:28 PM
 */
public class CaptchaView extends ContextAwareView {
	public static class Events extends EventType {

		protected Events( String code ) {
			super(code);
		}

		public static final Events Solution = new Events("CaptchaView.Events.Solution");
	}

	private Image image;
	private KBox imagePane;
	private KTextField textField;

	private String captchaUrl;
	private String captchaToken;
	private IEventListener callback;

	public CaptchaView(KindletContext context,
					   String captchaUrl,
					   String captchaToken,
					   IEventListener callback ) {
		super(context);

		this.captchaUrl = captchaUrl;
		this.captchaToken = captchaToken;
		this.callback = callback;
	}

	protected void buildUI() {
		try {
			this.setLayout( new KBoxLayout(this, KBoxLayout.Y_AXIS) );

			this.image = Toolkit.getDefaultToolkit().getImage( new URL(this.captchaUrl) );
			this.imagePane = KBox.createHorizontalBox();
			this.add( this.textField = new KTextField() );

			KButton submitButton = new KButton();
			submitButton.setLabel("Solve");
			submitButton.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					CaptchaView.this.callback.handleEvent( new AppEvent(
						CaptchaView.Events.Solution,
						new Object[] {
							CaptchaView.this.textField.getText()
						}
					) );
				}
			});

			this.add( submitButton );
		} catch ( MalformedURLException e ) {
			ViewFacade.showError("Runtime exception", "Unable to load image!");
		}
	}

	public void activate() {

	}

	protected void configUI() {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
