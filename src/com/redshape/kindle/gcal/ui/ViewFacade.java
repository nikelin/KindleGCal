package com.redshape.kindle.gcal.ui;

import com.amazon.kindle.kindlet.Kindlet;
import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.event.KindleKeyCodes;
import com.amazon.kindle.kindlet.ui.KOptionPane;
import com.redshape.kindle.gcal.core.event.AppEvent;
import com.redshape.kindle.gcal.core.event.EventType;
import com.redshape.kindle.gcal.core.event.IEventListener;
import com.redshape.kindle.gcal.core.utils.Registry;
import com.redshape.kindle.gcal.data.Calendar;
import com.redshape.kindle.gcal.ui.views.*;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.KeyEvent;

public class ViewFacade {
	private static final Logger log = Logger.getLogger(ViewFacade.class);

	public static class Events extends EventType {

		protected Events( String code ) {
			super(code);
		}

		public static final Events Init = new Events("ViewFacade.Events.Init");
		public static final Events Confirmation = new Events("ViewFacade.Events.Confirmation");
		public static final Events Input = new Events("ViewFacade.Events.Input");
	}

	private static ContextAwareView prevView;
	private static ContextAwareView currentView;

	private static KindletContext context;
	private static Kindlet kindlet;

	private static MainView MainView;
	private static ConfigView ConfigView;
	private static BrowseView BrowseView;
	private static AboutView AboutView;

	public static void init( final Kindlet kindlet,
							 final KindletContext context,
							 final IEventListener initHandler ) {
		ViewFacade.kindlet = kindlet;
		ViewFacade.context = context;

		Registry.init(context, new IEventListener() {
			public void handleEvent(AppEvent event) {
				KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
					new KeyEventDispatcher() {
						public boolean dispatchKeyEvent(KeyEvent e) {
							switch ( e.getKeyCode() ) {
								case KindleKeyCodes.VK_BACK:
									ViewFacade.back();
								break;
								case KindleKeyCodes.VK_RIGHT_HAND_SIDE_TURN_PAGE:
									Dispatcher.get().dispatch( new AppEvent(EventsView.Events.PreviousDay) );
								break;
								case KindleKeyCodes.VK_LEFT_HAND_SIDE_TURN_PAGE:
									Dispatcher.get().dispatch( new AppEvent(EventsView.Events.NextDay) );
								break;
							}

							return false;
						}
					}
				);

				try {
					initHandler.handleEvent( new AppEvent(Events.Init) );
				} catch ( Throwable e ) {
					log.error( e.getMessage(), e );
				}
			}
		});
	}

	public static void refresh() {
		context.getRootContainer().invalidate();
		context.getRootContainer().repaint();
	}

	public static void hideDialogs() {
		KOptionPane.dismissDialog( context.getRootContainer() );
	}

	public static boolean showConfirmation( String title, String message ) {
		try {
			KOptionPane.dismissDialog(context.getRootContainer());
			return KOptionPane.showConfirmDialog(context.getRootContainer(),
					 message, title ) == KOptionPane.OK_OPTION;
		} catch ( InterruptedException e ) {
			throw new RuntimeException( e.getMessage(), e );
		}
	}

	public static void showConfirmation( String title, String message,
										final IEventListener listener ) {
		KOptionPane.dismissDialog(context.getRootContainer());
		KOptionPane.showConfirmDialog( context.getRootContainer(),
					message, title, new KOptionPane.ConfirmDialogListener() {
			public void onClose(int i) {
				listener.handleEvent(
					new AppEvent( Events.Confirmation,
							new Object[] { Boolean.valueOf(i == KOptionPane.OK_OPTION) }
					)
				);
			}
		});
	}

	public static String showInputDialog( String title, String message ) {
		try {
			KOptionPane.dismissDialog(context.getRootContainer());
			return KOptionPane.showInputDialog( context.getRootContainer(),
					message, title );
		} catch ( InterruptedException e ) {
			throw new RuntimeException( e.getMessage(), e );
		}
	}

	public static void showInputDialog( String title,
										String message,
										final IEventListener listener ) {
		KOptionPane.dismissDialog(context.getRootContainer());
		KOptionPane.showInputDialog( context.getRootContainer(),
				message, title, new KOptionPane.InputDialogListener() {
			public void onClose(String s) {
				listener.handleEvent( new AppEvent(Events.Input, new Object[] { s } ) );
			}
		});
	}

	public static void showError( String title, String message ) {
		KOptionPane.dismissDialog( context.getRootContainer() );

		KOptionPane.showMessageDialog(
				ViewFacade.context.getRootContainer(),
				message, title,
				new KOptionPane.MessageDialogListener() {
					public void onClose() {
						ViewFacade.showMainView();
					}
				}
		);
	}

	public static void back() {
		if ( prevView == null ) {
			return;
		}

		try {
			if ( !EventQueue.isDispatchThread() ) {
				EventQueue.invokeLater(
					new Runnable() {
						public void run() {
							ViewFacade.activateView(prevView);
						}
					}
				);
			} else {
				ViewFacade.activateView(prevView);
			}
		} catch ( Throwable e ) {
			log.error( e.getMessage(), e );
		}
	}

	public static Kindlet kindlet() {
		return ViewFacade.kindlet;
	}

	private static void checkAssertions() {
		if ( context == null ) {
			throw new IllegalStateException("View facade has not been initialized yet.");
		}
	}

	private static void clearView() {
		checkAssertions();
		context.getRootContainer().removeAll();
		context.getRootContainer().invalidate();
		context.getRootContainer().repaint();
	}

	public static void showCaptchaView( String captchaUrl, String captchaToken, IEventListener callback ) {
		checkAssertions();
		activateView( new CaptchaView( context, captchaUrl, captchaToken, callback ) );
	}

	public static void showMainView() {
		checkAssertions();
		activateView( getMainView() );
	}

	private static MainView getMainView() {
		if ( MainView == null ) {
			MainView = new MainView(context);
		}

		return ViewFacade.MainView;
	}

	public static void showAboutView() {
		checkAssertions();
		activateView( getAboutView() );
	}

	protected static AboutView getAboutView() {
		if ( AboutView == null ) {
			AboutView = new AboutView(context);
		}

		return AboutView;
	}

	public static void showBrowseView() {
		checkAssertions();
		activateView( getBrowseView() );
	}

	protected static BrowseView getBrowseView() {
		if ( BrowseView == null ) {
			BrowseView = new BrowseView(context);
		}

		return BrowseView;
	}

	public static void showConfigView() {
		checkAssertions();
		activateView( getConfigView() );
	}

	protected static ConfigView getConfigView() {
		if ( ConfigView == null ) {
			ConfigView = new ConfigView(context);
		}

		return ConfigView;
	}

	public static void showEventsView( Calendar calendar ) {
		checkAssertions();
		activateView( new EventsView(context, calendar) );
	}

	private static void activateView( ContextAwareView view ) {
		clearView();
		prevView = currentView;
		currentView = view;
		context.getRootContainer().add( view );
		view.transferFocus();
		view.activate();
		context.getRootContainer().invalidate();
		context.getRootContainer().repaint();
	}

}
