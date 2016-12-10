package de.iolite.insys.mirror;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.api.IOLITEAPINotResolvableException;
import de.iolite.api.IOLITEAPIProvider;
import de.iolite.api.IOLITEPermissionDeniedException;
import de.iolite.app.AbstractIOLITEApp;
import de.iolite.app.api.frontend.FrontendAPI;
import de.iolite.app.api.frontend.FrontendAPIException;
import de.iolite.app.api.frontend.util.FrontendAPIUtility;
import de.iolite.app.api.storage.StorageAPI;
import de.iolite.app.api.storage.StorageAPIException;
import de.iolite.common.identifier.EntityIdentifier;
import de.iolite.common.lifecycle.exception.CleanUpFailedException;
import de.iolite.common.lifecycle.exception.InitializeFailedException;
import de.iolite.common.lifecycle.exception.StartFailedException;
import de.iolite.common.lifecycle.exception.StopFailedException;
import de.iolite.common.requesthandler.HTTPStatus;
import de.iolite.common.requesthandler.IOLITEHTTPRequest;
import de.iolite.common.requesthandler.IOLITEHTTPRequestHandler;
import de.iolite.common.requesthandler.IOLITEHTTPResponse;
import de.iolite.common.requesthandler.IOLITEHTTPStaticResponse;
import de.iolite.common.requesthandler.StaticResources;
import de.iolite.insys.mirror.api.MirrorApiException;
import de.iolite.insys.mirror.api.SimpleMirrorManager;

/**
 * @author Hendrik Motza
 */
public class MirrorExampleApp extends AbstractIOLITEApp {

	private static final Logger LOG = LoggerFactory.getLogger(MirrorExampleApp.class);
	private static final String HTML_RESOURCES = "de/iolite/insys/mirror/html";
	private static final String APP_ID = "de.iolite.insys.mirror.MirrorExampleApp";

	private static final String MSG_ERR_RETRIEVE_FRONTENDAPI = "Could not retrieve instance of FrontendAPI!";
	private static final String MSG_ERR_RETRIEVE_STORAGEAPI = "Could not retrieve instance of StorageAPI!";
	private static final String MSG_ERR_REGISTER_FRONTEND_RESOURCES = "An error appeared during the registration of frontend resources. This could result in errors when trying to display the app gui!";

	private static final String VIEW_ID_CLOCK = "DateTimeView";
	private static final String ICON_URL_CLOCK = "DateTimeView/clock-icon.jpg";
	private static final String VIEW_URL_CLOCK = "DateTimeView/clock.html";

	private static final String VIEW_ID_WELCOME = "WelcomeView";
	private static final String ICON_URL_WELCOME = "WelcomeView/welcome.png";
	private static final String VIEW_URL_WELCOME = "WelcomeView/welcome.html";

	private static final String VIEW_ID_HELLO_WORLD = "WelcomeWorldView";
	private static final String ICON_URL_HELLO_WORLD = "WelcomeView/welcome_friends.jpg";
	private static final String VIEW_URL_HELLO_WORLD = "WelcomeView/welcome.html?world=true";

	private static final String VIEW_ID_QUOTE = "QuoteView";
	private static final String ICON_URL_QUOTE = "QuoteView/quote.png";
	private static final String VIEW_URL_QUOTE = "QuoteView/quote.html";

	private static final String VIEW_ID_WEATHER = "WeatherView";
	private static final String ICON_URL_WEATHER = "WeatherView/weather.jpg";
	private static final String VIEW_URL_WEATHER = "WeatherView/DummyWeather.html";

	private static final String VIEW_ID_LNDW = "LNDWView";
	private static final String ICON_URL_LNDW = "LNDWView/lndw.png";
	private static final String VIEW_URL_LNDW = "LNDWView/lndw.html";

	private Quote quoteOfTheDay = null;
	private ScheduledFuture<?> quoteUpdateThread = null;

	private SimpleMirrorManager mirrorManager;

	private static final List<Quote> quotes = new ArrayList<Quote>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void cleanUpHook()
			throws CleanUpFailedException {
		// nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeHook()
			throws InitializeFailedException {
		quotes.add(new Quote("Wer kämpft, der kann verlieren, wer nicht kämpft, hat schon verloren", "Unbekannt"));
		quotes.add(new Quote("Wer sich nicht mehr wundern kann, ist seelisch bereits tot", "Albert Einstein"));
		quotes.add(new Quote("Wer einen Fehler gemacht hat und ihn nicht korrigiert, begeht einen zweiten", "Konfuzius"));
		quotes.add(new Quote("Entscheide lieber ungefähr richtig, als genau falsch", "Goethe"));
		quotes.add(new Quote("Der Schwache kann nicht verzeihen. Verzeihen ist eine Eigenschaft des Starken", "Mahatma Gandhi"));
		quotes.add(new Quote("Wenn du weißt, dass du etwas vermasseln wirst, dann vermassel es wenigstens ordentlich", "Cate Blanchett "));
		quotes.add(new Quote("Sei, was du scheinen willst", "Sokrates"));
		quotes.add(new Quote("Wenn du den wahren Charakter eines Menschen erkennen willst, dann gib ihm Macht", "Abraham Lincoln"));
		quotes.add(new Quote("Von einem Baum, der noch in Blüte steht, musst du nicht schon Früchte erwarten", "Karl Ferdinand Gutzkow"));
		quotes.add(new Quote("Wer an der Küste bleibt, kann keine neuen Ozeane entdecken.", "Fernando Magellan"));
		quotes.add(new Quote("Auch Umwege erweitern unseren Horizont.", "Ernst Ferstl"));
		quotes.add(new Quote("Jeder Tag, an dem du nicht lächelst, ist ein verlorener Tag.", "Charlie Chaplin"));
		quotes.add(new Quote("Gib jedem Tag die Chance, der schönste deines Lebens zu werden.", "Mark Twain"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void startHook(final IOLITEAPIProvider context)
			throws StartFailedException {
		final FrontendAPI frontendApi;
		try {
			frontendApi = context.getAPI(FrontendAPI.class);
		}
		catch (final Exception e) {
			LOG.error(MSG_ERR_RETRIEVE_FRONTENDAPI, e);
			throw new StartFailedException(MSG_ERR_RETRIEVE_FRONTENDAPI, e);
		}

		final StorageAPI storageApi;
		try {
			storageApi = context.getAPI(StorageAPI.class);
		}
		catch (final Exception e) {
			LOG.error(MSG_ERR_RETRIEVE_STORAGEAPI, e);
			throw new StartFailedException(MSG_ERR_RETRIEVE_STORAGEAPI, e);
		}

		try {
			FrontendAPIUtility.registerPublicHandlers(frontendApi, StaticResources.scanClasspath(HTML_RESOURCES, getClass().getClassLoader()));
			frontendApi.registerPublicClasspathStaticResource("", HTML_RESOURCES + "/index.html");
			frontendApi.registerPublicRequestHandler(VIEW_URL_WELCOME, createHelloWorldRequestHandler(storageApi));
			frontendApi.registerPublicRequestHandler(VIEW_URL_QUOTE, createQuoteRequestHandler());
		}
		catch (final FrontendAPIException e) {
			LOG.error(MSG_ERR_REGISTER_FRONTEND_RESOURCES, e);
		}

		try {
			this.mirrorManager = new SimpleMirrorManager(context, APP_ID);
			this.mirrorManager.createView(VIEW_ID_CLOCK, ICON_URL_CLOCK, VIEW_URL_CLOCK, "Clock", false);
			this.mirrorManager.createView(VIEW_ID_WEATHER, ICON_URL_WEATHER, VIEW_URL_WEATHER, "Weather", false);
			this.mirrorManager.createView(VIEW_ID_LNDW, ICON_URL_LNDW, VIEW_URL_LNDW, "Lange Nacht der Wissenschaften", false);
			this.mirrorManager.createView(VIEW_ID_WELCOME, ICON_URL_WELCOME, VIEW_URL_WELCOME, "Greeting");
			this.mirrorManager.createView(VIEW_ID_HELLO_WORLD, ICON_URL_HELLO_WORLD, VIEW_URL_HELLO_WORLD, "Greeting", false);
		}
		catch (final IOLITEAPINotResolvableException e) {
			LOG.error("Could not provide views for mirrors! Please make sure that your IOLITE version is up to date!", e);
		}
		catch (final IOLITEPermissionDeniedException e) {
			LOG.error("Could not provide views for mirrors! App misses neccessary permissions!", e);
		}
		catch (final MirrorApiException e) {
			LOG.error("Could not create views!", e);
		}

		this.quoteUpdateThread = context.getScheduler().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					final long position = Math.round(Math.random() * (quotes.size() - 1));
					MirrorExampleApp.this.quoteOfTheDay = quotes.get((int) position);
					MirrorExampleApp.this.mirrorManager.createView(VIEW_ID_QUOTE, ICON_URL_QUOTE,
							VIEW_URL_QUOTE + "?timestamp=" + System.currentTimeMillis(), "Quote of the day");
				}
				catch (final MirrorApiException e) {
					LOG.error("Could not create views!", e);
				}
			}
		}, 0, 1, TimeUnit.MINUTES);
		LOG.debug("Mirror Views got registered!");

	}

	private IOLITEHTTPRequestHandler createHelloWorldRequestHandler(final StorageAPI storageApi) {
		return new IOLITEHTTPRequestHandler() {

			@Override
			public void handlerRemoved(final String mapping, final EntityIdentifier callerEntityID) {
				// nothing to do

			}

			@Override
			public IOLITEHTTPResponse handleRequest(final IOLITEHTTPRequest request, final EntityIdentifier callerEntityID, final String subPath) {
				try {
					final InputStream is = this.getClass().getClassLoader().getResourceAsStream(HTML_RESOURCES + "/" + VIEW_URL_WELCOME);
					if (is == null) {
						LOG.error("Resource not found: {}", HTML_RESOURCES + "/" + VIEW_URL_WELCOME);
						return new IOLITEHTTPStaticResponse(HTTPStatus.NotFound, IOLITEHTTPResponse.HTML_CONTENT_TYPE);
					}
					final String template = IOUtils.toString(is, "UTF-8");
					String replacement;
					if (Boolean.valueOf(request.getParameter("world"))) {
						replacement = "Welt";
					}
					else {
						try {
							replacement = storageApi.loadString("uName");
						}
						catch (final StorageAPIException e) {
							replacement = "";
						}
					}
					return new IOLITEHTTPStaticResponse(template.replaceFirst(Pattern.quote("{USERNAME}"), replacement),
							IOLITEHTTPResponse.HTML_CONTENT_TYPE);
				}
				catch (final Exception e) {
					LOG.error(e.getMessage(), e);
					return null;
				}
			}
		};
	}

	private IOLITEHTTPRequestHandler createQuoteRequestHandler() {

		return new IOLITEHTTPRequestHandler() {

			@Override
			public void handlerRemoved(final String mapping, final EntityIdentifier callerEntityID) {
				// nothing to do

			}

			@Override
			public IOLITEHTTPResponse handleRequest(final IOLITEHTTPRequest request, final EntityIdentifier callerEntityID, final String subPath) {
				try {
					final InputStream is = this.getClass().getClassLoader().getResourceAsStream(HTML_RESOURCES + "/" + VIEW_URL_QUOTE);
					if (is == null) {
						LOG.error("Resource not found: {}", HTML_RESOURCES + "/" + VIEW_URL_QUOTE);
						return new IOLITEHTTPStaticResponse(HTTPStatus.NotFound, IOLITEHTTPResponse.HTML_CONTENT_TYPE);
					}
					final String template = IOUtils.toString(is, "UTF-8");
					return new IOLITEHTTPStaticResponse(
							template.replaceFirst(Pattern.quote("{QUOTE}"), MirrorExampleApp.this.quoteOfTheDay.getQuote())
									.replaceFirst(Pattern.quote("{AUTHOR}"), MirrorExampleApp.this.quoteOfTheDay.getAuthor()),
							IOLITEHTTPResponse.HTML_CONTENT_TYPE);
				}
				catch (final Exception e) {
					LOG.error(e.getMessage(), e);
					return null;
				}
			}
		};

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void stopHook()
			throws StopFailedException {
		LOG.debug("Stopping APP ");
		this.quoteUpdateThread.cancel(false);
		if (this.mirrorManager != null) {
			this.mirrorManager.stopHook();
		}
	}

}
