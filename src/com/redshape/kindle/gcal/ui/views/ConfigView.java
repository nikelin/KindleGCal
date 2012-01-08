package com.redshape.kindle.gcal.ui.views;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.*;
import com.amazon.kindle.kindlet.ui.border.KLineBorder;
import com.redshape.kindle.gcal.core.data.store.state.StateEvents;
import com.redshape.kindle.gcal.core.data.store.state.StateManager;
import com.redshape.kindle.gcal.core.event.AppEvent;
import com.redshape.kindle.gcal.core.event.IEventListener;
import com.redshape.kindle.gcal.core.utils.Constants;
import com.redshape.kindle.gcal.core.utils.Registry;
import com.redshape.kindle.gcal.ui.ContextAwareView;
import com.redshape.kindle.gcal.ui.Dispatcher;
import com.redshape.kindle.gcal.ui.ViewFacade;
import org.apache.log4j.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

public class ConfigView extends ContextAwareView {
	private static final Logger log = Logger.getLogger( ConfigView.class );

	private KTextField accountId;
	private KPasswordField password;
	
	public ConfigView( KindletContext context ) {
		super(context);

		this.bindEvents();
	}

	protected void bindEvents() {
		this.addComponentListener(
			new ComponentAdapter() {
				public void componentShown(ComponentEvent e) {
					ConfigView.this.accountId.requestFocus();
					ConfigView.this.accountId.requestFocusInWindow();
				}
			}
		);

		Dispatcher.get().addListener(
			StateEvents.Restore,
			new IEventListener() {
				public void handleEvent(AppEvent event) {
					String login = (String) Registry.get(Registry.Attribute.Login);
					ConfigView.this.accountId.setText( login == null ? "" : login.toString() );

					String password = (String) Registry.get(Registry.Attribute.Password);
					ConfigView.this.password.setText( password == null ? "" : password.toString() );

					ConfigView.this.invalidate();
					ConfigView.this.repaint();
				}
			}
		);
	}

	protected void buildUI() {
		this.setLayout( new KBoxLayout( this, KBoxLayout.Y_AXIS ) );

		KBox accountIdField = KBox.createHorizontalBox();
		accountIdField.add(new KLabel("Google ID:"));
		this.accountId = new KTextField();
		String login = (String) Registry.get(Registry.Attribute.Login);
		this.accountId.setText( login == null ? "" : login.toString() );
		this.accountId.setSize( 135, 20 );
		this.accountId.setBorder( new KLineBorder(1) );
		accountIdField.add(this.accountId);
		this.add( accountIdField );

		KBox passwordField = KBox.createHorizontalBox();
		passwordField.add( new KLabel("Password:") );
		this.password = new KPasswordField();
		String password = (String) Registry.get(Registry.Attribute.Password);
		this.password.setText( password == null ? "" : password.toString() );
		this.password.setSize( 135, 20 );
		this.password.setBorder( new KLineBorder(1) );
		passwordField.add( this.password );
		this.add(passwordField);

		KBox refreshRateField = KBox.createHorizontalBox();
		refreshRateField.add(new KLabel("Refresh rate:"));
		refreshRateField.add(new KLabel( String.format("%s minutes",
				new Object[] {
					String.valueOf(
						( (Long) Registry.get(Registry.Attribute.RefreshTime) ).longValue() /
							new Long( Constants.TIME_MINUTE ).longValue()
					)
				}) ) );
		this.add(refreshRateField);

		KBox refreshModeField = KBox.createHorizontalBox();
		refreshModeField.add( new KLabel("Enabled automatic synchronization:"));
		final KButton toogleModeButton = new KButton();
		toogleModeButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean state = toogleModeButton.getLabel().equals("Enabled");
					Registry.set(Registry.Attribute.RefreshEnabled, Boolean.valueOf( !state ) );

					if ( state ) {
						toogleModeButton.setLabel("Disabled");
					} else {
						toogleModeButton.setLabel("Enabled");
						Registry.requestConnectivity();
					}
				}
			}
		);
		if ( Registry.get(Registry.Attribute.RefreshEnabled ).equals(Boolean.TRUE) ) {
			toogleModeButton.setLabel("Enabled");
		} else {
			toogleModeButton.setLabel("Disabled");
		}
		refreshModeField.add( toogleModeButton );
		this.add(refreshModeField);

		KButton saveButton = new KButton("Save");
		saveButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ConfigView.this.onSave();
				}
			}
		);

		this.add(saveButton);
	}

	public void activate() {
		this.accountId.setText( Registry.get(Registry.Attribute.Login) == null ? "" :
							Registry.get(Registry.Attribute.Login).toString() );
		this.password.setText( Registry.get(Registry.Attribute.Password) == null ? "" :
							Registry.get(Registry.Attribute.Password).toString() );
	}

	protected void onSave() {
		Registry.set(Registry.Attribute.Login, this.accountId.getText() );
		Registry.set(Registry.Attribute.Password, String.valueOf(this.password.getPassword()) );

		ViewFacade.showConfirmation("Browse", "Proceed to calendars list?",
			new IEventListener() {
				public void handleEvent(AppEvent event) {
					ViewFacade.showBrowseView();
				}
			}
		);

		try {
			StateManager.getDefault().save();
		} catch ( IOException e ) {
			log.error( e.getMessage(), e );
		}
	}

	protected void configUI() {
	}
	
}
