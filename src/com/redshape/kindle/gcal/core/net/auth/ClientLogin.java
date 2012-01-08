package com.redshape.kindle.gcal.core.net.auth;

import com.amazon.kindle.kindlet.KindletContext;
import com.redshape.kindle.gcal.AppKindlet;
import com.redshape.kindle.gcal.core.event.AppEvent;
import com.redshape.kindle.gcal.core.event.EventDispatcher;
import com.redshape.kindle.gcal.core.event.EventType;
import com.redshape.kindle.gcal.core.event.IEventListener;
import com.redshape.kindle.gcal.core.net.http.HttpRequest;
import com.redshape.kindle.gcal.core.net.http.HttpResponse;
import com.redshape.kindle.gcal.core.utils.Registry;
import com.redshape.kindle.gcal.ui.ViewFacade;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 * @package com.redshape.kindle.gcal.core.net.auth
 * @date 11/17/11 3:35 PM
 */
public class ClientLogin extends EventDispatcher implements IAuthenticator {
	public static class Events extends EventType {

		protected Events(String code) {
			super(code);
		}

		public static final Events Complete = new Events("ClientLogin.Events.Complete");
	}

	private static final Logger log = Logger.getLogger( ClientLogin.class );
	private KindletContext context;

	protected interface Request {

		public void proceed();

		public Map getAttributes();

		public void setAttributes( Map attributes );

		public void setAttribute( String name, Object value );

		public Object getAttribute( String name );

	}

	protected static abstract class AbstractRequest implements Request {
		private Map attributes = new HashMap();
		private ClientLogin context;
		private Request request;

		public AbstractRequest( ClientLogin context ) {
			this(context, null);
		}

		public AbstractRequest( ClientLogin context, Request request ) {
			this.context = context;
			this.request = request;
		}

		public Map getAttributes() {
			return attributes;
		}

		public void setAttributes(Map attributes) {
			this.attributes.putAll(attributes);
		}

		public void setAttribute(String name, Object value) {
			this.attributes.put(name, value);
		}

		public Object getAttribute(String name) {
			return this.attributes.get(name);
		}

		public ClientLogin getContext() {
			return context;
		}

		public Request getRequest() {
			return request;
		}
	}

	protected static class CaptchaRequest extends AbstractRequest {
		private HttpRequest httpRequest;

		public CaptchaRequest( ClientLogin context, HttpRequest request ) {
			super( context );

			this.httpRequest = request;
		}

		public void proceed() {
			ViewFacade.showCaptchaView(
					String.valueOf( CaptchaRequest.this.getAttribute("captchaUrl") ),
					String.valueOf( CaptchaRequest.this.getAttribute("captchaToken") ),
					new IEventListener() {
						public void handleEvent(AppEvent event) {
							String solution = (String) event.getArg(0);
							if ( solution.isEmpty() || solution == null ) {
								ViewFacade.showError("Wrong solution", "Captcha solution must not be void");
								return;
							}

							ProceedRequest request = new ProceedRequest( CaptchaRequest.this.getContext(),
									CaptchaRequest.this.httpRequest );
							request.setAttributes( CaptchaRequest.this.getAttributes() );
							request.setAttribute("captchaSolution", solution);

							CaptchaRequest.this.getContext().execute( request );
						}
					}
			);
		}
	}

	protected static class ProceedRequest extends AbstractRequest {
		private static String LOGIN_URL = "https://www.google.com/accounts/ClientLogin";
		private static String DEFAULT_QUERY = "Email=%s&source=" + AppKindlet.VENDOR +
				"&Passwd=%s&accountType=GOOGLE&service=cl";
		private static String CAPTCHA_QUERY = DEFAULT_QUERY + "&logintoken=%s&logincaptcha=%s";
		private HttpRequest httpRequest;

		public ProceedRequest( ClientLogin context, HttpRequest request ) {
			this(context, request, null);
		}

		public ProceedRequest( ClientLogin context, HttpRequest httpRequest, Request request ) {
			super(context, request);

			this.httpRequest = httpRequest;
		}

		public void proceed() {
			boolean success = true;
			try {
				final String requestUrl = LOGIN_URL;
				final String query;
				if ( this.getAttribute("captchaUrl") == null ) {
					query = String.format( DEFAULT_QUERY, new String[] {
							String.valueOf( Registry.get(Registry.Attribute.Login) ),
							String.valueOf( Registry.get(Registry.Attribute.Password) )
					} );
				} else {
					query = String.format( CAPTCHA_QUERY, new String[] {
							String.valueOf( Registry.get(Registry.Attribute.Login) ),
							String.valueOf( Registry.get(Registry.Attribute.Password) ),
							String.valueOf( this.getAttribute("captchaToken") ),
							String.valueOf( this.getAttribute("captchaSolution") )
					});
				}

				String urlString = requestUrl + "?" + query;
				log.info("Requesting " + urlString );
				URL url = new URL( urlString );
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setInstanceFollowRedirects(true);
				connection.setFixedLengthStreamingMode( query.length() );

				try {
					connection.connect();
				} catch ( IOException e ) {
					log.error( e.getMessage(), e );
				}

				connection.getOutputStream().write( '\0' );
				HttpResponse response = HttpResponse.valueOf(connection);
				String[] fields = response.getBody().split("\n");

				String captchaToken = null;
				String captchaUrl = null;
				String error = null;
				String sessionId = null;
				for ( int i = 0; i < fields.length; i++ ) {
					String[] parts = fields[i].split("=");
					String value = parts[1];
					String key = parts[0];

					if ( key.equals("Error") ) {
						error = value;
					} else if ( key.equals("Auth") ) {
						sessionId = value;
						break;
					} else if ( key.equals("CaptchaToken") ) {
						captchaToken = value;
					} else if ( key.equals("CaptchaUrl") ) {
						captchaUrl = value;
					}
				}

				if ( error != null || sessionId == null ) {
					if ( captchaToken == null || captchaUrl == null ) {
						ViewFacade.showError("Service error", error);
						return;
					}

					CaptchaRequest request = new CaptchaRequest( ProceedRequest.this.getContext(),
							ProceedRequest.this.httpRequest );
					request.setAttributes( ProceedRequest.this.getAttributes() );
					request.setAttribute("captchaToken", captchaToken );
					request.setAttribute("captchaUrl", captchaUrl );

					ProceedRequest.this.getContext().execute( request );
				} else {
					ProceedRequest.this.httpRequest.setHeader("Authorization", "GoogleLogin auth=" + sessionId);

					IEventListener listener = (IEventListener) ProceedRequest.this.getAttribute("listener");
					AppEvent event = new AppEvent(Events.Complete, new Object[] {
							ProceedRequest.this.httpRequest,
							listener
					});

					if ( listener == null ) {
						ProceedRequest.this.getContext().dispatch(event);
					} else {
						listener.handleEvent(event);
					}
				}

				if ( ProceedRequest.this.getRequest() != null ) {
					ProceedRequest.this.getRequest().setAttribute("sid", sessionId );
					ProceedRequest.this.getContext().execute(ProceedRequest.this.getRequest());
				}
			} catch ( MalformedURLException e ) {
				log.error( e.getMessage(), e );
				ViewFacade.showError(e.getMessage(), "Internal" );
				success = false;
			} catch ( IOException e ) {
				log.error( e.getMessage(), e );
				ViewFacade.showError( e.getMessage(), "Internal" );
				success = false;
			} catch ( URISyntaxException e ) {
				log.error( e.getMessage(), e );
				ViewFacade.showError( e.getMessage(), "Internal" );
				success = false;
			} catch ( Throwable e ) {
				log.error( e.getMessage(), e );
			}

			if ( !success ) {
				Registry.set( Registry.Attribute.Login, null );
				Registry.set( Registry.Attribute.Password, null );

				ViewFacade.showConfirmation(
						"Invalid authentication credentials provided! Do you want to re-enter them?",
						"Auth",
						new IEventListener() {
							public void handleEvent(AppEvent event) {
								try {
									Boolean result = (Boolean) event.getArg(0);
									if ( result.equals(Boolean.TRUE) ) {
										ProceedRequest.this.getContext().authorize(
												ProceedRequest.this.httpRequest);
									} else {
										ViewFacade.showMainView();
									}
								} catch ( Throwable e ) {
									log.error( e.getMessage(), e );
								}
							}
						}
				);
			}

		}
	}

	protected static class LoginRequest extends AbstractRequest {
		public LoginRequest(ClientLogin context) {
			this(context, null);
		}

		public LoginRequest(ClientLogin context, Request request) {
			super(context, request);
		}

		public void proceed() {
			ViewFacade.showInputDialog(
					"Sign in",
					"Enter your Google ID",
					new IEventListener() {
						public void handleEvent( AppEvent event ) {
							String s = (String) event.getArg(0);
							if ( s.isEmpty() ) {
								ViewFacade.showError("Auth", "Login must not be empty");
								return;
							}

							Request request = LoginRequest.this.getRequest();
							if ( request == null ) {
								return;
							}

							Registry.set(Registry.Attribute.Login, s);
							request.setAttributes(LoginRequest.this.getAttributes());
							request.setAttribute("login", s);

							LoginRequest.this.getContext().execute(request);
						}
					}
			);
		}
	}

	protected static class PasswordRequest extends AbstractRequest {
		public PasswordRequest(ClientLogin context) {
			this(context, null);
		}

		public PasswordRequest(ClientLogin context, Request request) {
			super(context, request);
		}

		public void proceed() {
			ViewFacade.showInputDialog(
					"Sign in",
					"Enter your account password",
					new IEventListener() {
						public void handleEvent(AppEvent event) {
							String s = (String) event.getArg(0);
							if ( s.isEmpty() ) {
								ViewFacade.showError("Auth", "Password must not be empty!");
								return;
							}

							Request request = PasswordRequest.this.getRequest();
							if ( request == null ) {
								return;
							}

							Registry.set(Registry.Attribute.Password, s );
							request.setAttributes( PasswordRequest.this.getAttributes() );
							request.setAttribute("password", s);

							PasswordRequest.this.getContext().execute(request);
						}
					}
			);
		}
	}


	public ClientLogin( KindletContext context ) {
		this.context = context;
	}

	protected void execute( final Request request ) {
		request.proceed();
	}

	public void authorize(HttpRequest request) throws AuthenticatorException {
		this.authorize(request, null);
	}

	public void authorize(HttpRequest request, IEventListener listener) throws AuthenticatorException {
		String login = (String) Registry.get(Registry.Attribute.Login);
		String password = (String) Registry.get(Registry.Attribute.Password);

		Request command = new ProceedRequest(this, request);
		command.setAttribute("listener", listener);
		if ( password == null || password.isEmpty() ) {
			log.info("Password is empty");
			command = new PasswordRequest(this, command);
		} else {
			log.info("Password is: " + password );
		}

		if ( login == null || login.isEmpty() ) {
			log.info("Login is empty");
			command = new LoginRequest(this, command);
		} else {
			log.info("Login is: " + login );
		}

		this.execute( command );
	}

}
