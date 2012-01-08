package com.redshape.kindle.gcal.ui.views;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KBox;
import com.amazon.kindle.kindlet.ui.KBoxLayout;
import com.amazon.kindle.kindlet.ui.KLabel;
import com.redshape.kindle.gcal.core.data.store.StoreManager;
import com.redshape.kindle.gcal.core.utils.Registry;
import com.redshape.kindle.gcal.data.stores.CalendarStore;
import com.redshape.kindle.gcal.ui.ContextAwareView;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainView extends ContextAwareView {
	private DateFormat formatter = new SimpleDateFormat("dd'th' E, M");

	public MainView( KindletContext context ) {
		super( context );
	}

	protected void buildUI() {
		this.addFocusListener(
			new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					MainView.this.requestFocus();
				}
			}
		);

		this.setLayout( new KBoxLayout(this, KBoxLayout.Y_AXIS ));

		KLabel label = new KLabel("Google Calendar for Kindle");
		label.setHorizontalAlignment( KLabel.CENTER );
		label.setVerticalAlignment(KLabel.CENTER);
		label.setBounds( 100, 100, 100, 100 );
		Font font = new Font("Arial", Font.BOLD, 35);
		label.setFont(font);
		this.add( label );

		KBox currentDate = KBox.createHorizontalBox();
		currentDate.add( new KLabel("Now:") );
		currentDate.add( new KLabel( new Date().toString() ) );
		this.add( currentDate );

		KBox lastSyncTime = KBox.createHorizontalBox();
		lastSyncTime.add( new KLabel("Last synchronization date:") );
		lastSyncTime.add( new KLabel(
				Registry.get(Registry.Attribute.LastSync) == null ? "Never"
						: this.formatter.format(( Date ) Registry.get(Registry.Attribute.LastSync))
		) );
		this.add(lastSyncTime);


		KBox calendarsCount = KBox.createHorizontalBox();
		calendarsCount.add( new KLabel("Calendars count:") );
		calendarsCount.add( new KLabel(
				String.valueOf( StoreManager.getDefault().getStore(CalendarStore.class).count() ) ) );
		this.add( calendarsCount );

//		this.browseButton = new KButton("Browse");
//		this.browseButton.addActionListener(
//				new ActionListener() {
//					public void actionPerformed(ActionEvent e) {
//						ViewFacade.showBrowseView();
//					}
//				}
//		);
//		this.add(this.browseButton);
//
//		KButton configButton = new KButton("Setting");
//		configButton.addActionListener(
//			new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					ViewFacade.showConfigView();
//				}
//			}
//		);
//		this.add( configButton );
//
//		KButton aboutButton = new KButton("About");
//		aboutButton.addActionListener(
//			new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					ViewFacade.showAboutView();
//				}
//			}
//		);
//		this.add( aboutButton );
//
//		KButton exitButton = new KButton("Exit");
//		exitButton.addActionListener(
//			new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					KOptionPane.showConfirmDialog( MainView.this,
//						"Do you really want to close application?",
//						new KOptionPane.ConfirmDialogListener() {
//							public void onClose(int i) {
//								if ( i == KOptionPane.OK_OPTION ) {
//									ViewFacade.kindlet().destroy();
//								}
//							}
//						}
//					);
//				}
//			}
//		);
//		this.add( exitButton );
	}

	public void activate() {

	}

	protected void configUI() {

	}
	
}
