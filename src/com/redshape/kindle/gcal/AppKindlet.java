package com.redshape.kindle.gcal;

import com.amazon.kindle.kindlet.AbstractKindlet;
import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KMenu;
import com.amazon.kindle.kindlet.ui.KMenuItem;
import com.redshape.kindle.gcal.core.event.AppEvent;
import com.redshape.kindle.gcal.core.event.IEventListener;
import com.redshape.kindle.gcal.core.utils.Registry;
import com.redshape.kindle.gcal.ui.ViewFacade;
import com.redshape.kindle.gcal.ui.panels.RefreshTimePane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AppKindlet extends AbstractKindlet {
	public static final String VENDOR = "Redshape-KindleGCal-0.1";

	private KindletContext context;

	public void create(final KindletContext context) {
		this.context = context;

		context.setMenu( this.createMenu() );

		context.setSubTitle("Kindle GCal");
		context.setTextOptionPane(new RefreshTimePane());

		ViewFacade.init(this, context,
				new IEventListener() {
					public void handleEvent(AppEvent event) {
						Registry.startStateFlushThread();

						ViewFacade.showMainView();
					}
				}
		);
	}

	protected KMenu createMenu() {
		KMenu menu = new KMenu();

		KMenuItem browseItem = new KMenuItem("Browse");
		browseItem.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ViewFacade.showBrowseView();
					}
				}
		);
		menu.add( browseItem );

		KMenuItem homeItem = new KMenuItem( "Home" );
		homeItem.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ViewFacade.showMainView();
				}
			}
		);
		menu.add( homeItem );

		KMenuItem settingsItem = new KMenuItem( "Settings" );
		settingsItem.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ViewFacade.showConfigView();
				}
			}
		);
		menu.add( settingsItem );

		KMenuItem aboutItem = new KMenuItem( "About" );
		aboutItem.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ViewFacade.showAboutView();
				}
			}
		);
		menu.add( aboutItem );

		return menu;
	}

}
