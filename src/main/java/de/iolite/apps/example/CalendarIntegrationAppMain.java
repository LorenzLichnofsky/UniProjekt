package de.iolite.apps.example;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.api.IOLITEAPINotResolvableException;
import de.iolite.api.IOLITEAPIProvider;
import de.iolite.api.IOLITEPermissionDeniedException;
import de.iolite.app.AbstractIOLITEApp;
import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceAPI;
import de.iolite.app.api.device.access.DeviceAPI.DeviceAPIObserver;
import de.iolite.app.api.device.access.DeviceStringProperty.DeviceStringPropertyObserver;
import de.iolite.app.api.device.access.DeviceDoubleProperty;
import de.iolite.app.api.device.access.DeviceStringProperty;
import de.iolite.app.api.environment.EnvironmentAPI;
import de.iolite.app.api.frontend.FrontendAPI;
import de.iolite.app.api.frontend.FrontendAPIException;
import de.iolite.app.api.frontend.util.FrontendAPIUtility;
import de.iolite.app.api.storage.StorageAPI;
import de.iolite.app.api.storage.StorageAPIException;
import de.iolite.app.api.user.access.UserAPI;
import de.iolite.apps.example.ViewRegistrator.ResourcePackageConfig;
import de.iolite.apps.example.ViewRegistrator.TemplateConfig;
import de.iolite.apps.example.controller.EnvironmentController;
import de.iolite.apps.example.controller.StorageController;
import de.iolite.apps.example.devices.SonosController;
import de.iolite.apps.example.internals.PageWithEmbeddedSessionTokenRequestHandler;
import de.iolite.common.lifecycle.exception.CleanUpFailedException;
import de.iolite.common.lifecycle.exception.InitializeFailedException;
import de.iolite.common.lifecycle.exception.StartFailedException;
import de.iolite.common.lifecycle.exception.StopFailedException;
import de.iolite.common.requesthandler.IOLITEHTTPRequestHandler;
import de.iolite.common.requesthandler.StaticResources;
import de.iolite.common.requesthandler.StaticResources.PathHandlerPair;
import de.iolite.data.DailyEvents;
import de.iolite.data.GoogleData;
import de.iolite.drivers.basic.DriverConstants;
import de.iolite.insys.mirror.api.MirrorApiException;
import de.iolite.utilities.disposeable.Disposeable;
import de.iolite.utilities.concurrency.scheduler.Scheduler;

/**
 * Orginal code from: <code>ExampleApp</code> is an example IOLITE App.
 *
 * @author Grzegorz Lehmann
 * @author Erdene-Ochir Tuguldur
 * @author Felix Rodemund edit by Group Calendar Integration
 * @since 1.0
 * 
 *        adjustments made by Calendar Group
 */
public final class CalendarIntegrationAppMain extends AbstractIOLITEApp {

	private static final class DeviceAddAndRemoveLogger implements DeviceAPIObserver {

		@Override
		public void addedToDevices(final Device device) {
			LOGGER.debug("a new device added '{}'", device.getIdentifier());
		}

		@Override
		public void removedFromDevices(final Device device) {
			LOGGER.debug("a device removed '{}'", device.getIdentifier());
		}
	}

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(CalendarIntegrationAppMain.class);

	/** App APIs */
	private FrontendAPI frontendAPI;
	private StorageAPI storageAPI;
	private DeviceAPI deviceAPI;
	private EnvironmentAPI environmentAPI;
	private UserAPI userAPI;

	/** front end assets */
	private Disposeable disposeableAssets;

	/** sonos assets */
	SonosController sonosController = new SonosController();
	StorageController storageController;
	EnvironmentController environmentController;

	/**
	 * Mirror variables basic idea is taken over from Hendrik Motza from
	 * Calendar Group
	 */
	private static final String MSG_ERR_RETRIEVE_USERAPI = "Could not retrieve instance of UserAPI!";

	private static final String VIEW_RESOURCES = "assets/views/";
	private static final String APP_ID = "de.iolite.apps.example.CalendarIntegrationAppMain";

	private static final String VIEW_ID_CALENDAR = "CalendarView";
	private static final String ICON_RESPATH_CALENDAR = VIEW_RESOURCES + "calendar-icon.jpg";
	private static final String VIEW_RESPATH_CALENDAR = VIEW_RESOURCES + "calendar.html";
	private static final String VIEW_TEMPLATE_CALENDAR = VIEW_RESOURCES + "calendar.template";
	private static final String VIEW_WEBPATH_CALENDAR = "calendar.html";

	private static final String VIEW_ID_CLOCK = "DateTimeView";
	private static final String ICON_RESPATH_CLOCK = VIEW_RESOURCES + "clock-icon.jpg";
	private static final String VIEW_RESPATH_CLOCK = VIEW_RESOURCES + "clock.html";
	private static final String VIEW_WEBPATH_CLOCK = "clock.html";

	private static final String VIEW_ID_TRAFFIC = "TrafficView";
	private static final String ICON_RESPATH_TRAFFIC = VIEW_RESOURCES + "traffic-icon.jpg";
	private static final String VIEW_RESPATH_TRAFFIC = VIEW_RESOURCES + "traffic.html";
	private static final String VIEW_WEBPATH_TRAFFIC = "traffic.html";
	
	private static final String VIEW_RESPATH_EMPTY_TRAFFIC = VIEW_RESOURCES + "empty_traffic.html";
	private static final String VIEW_RESPATH_NO_APPOINTMENT_TRAFFIC = VIEW_RESOURCES + "no_appointment_traffic.html";

	private static final String VIEW_ID_WEATHER = "WeatherView";
	private static final String ICON_RESPATH_WEATHER = VIEW_RESOURCES + "weather-icon.jpg";
	private static final String VIEW_RESPATH_WEATHER = VIEW_RESOURCES + "weather.html";
	private static final String VIEW_WEBPATH_WEATHER = "weather.html";

	DailyEvents calendar = null;
	
	Scheduler scheduler;
	private ScheduledFuture<?> calendarUpdateThread = null;

	private ViewRegistrator viewRegistrator;

	String temperature = "0";

	/**
	 * <code>ExampleApp</code> constructor. An IOLITE App must have a public,
	 * parameter-less constructor.
	 */
	public CalendarIntegrationAppMain() {
		// empty
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void cleanUpHook() throws CleanUpFailedException {
		LOGGER.debug("Cleaned");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeHook() throws InitializeFailedException {
		LOGGER.debug("Initialized");

		try {
			/** Read out of Google Data */
			GoogleData data = new GoogleData();
			this.calendar = data.getData();
			LOGGER.info(this.calendar.toString());
		} catch (IOException | ParseException | GeneralSecurityException | URISyntaxException e) {
			LOGGER.error("ERROR while getting calendar.getData()");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void startHook(@Nonnull final IOLITEAPIProvider context) throws StartFailedException {
		// here the IOLITE App is started
		// the context gives access to IOLITE App APIs
		LOGGER.debug("Starting");

		try {

			// Scheduler
			this.scheduler = context.getScheduler();

			// use User API
			this.userAPI = context.getAPI(UserAPI.class);
			LOGGER.debug("Running for user '{}' with locale '{}'", this.userAPI.getUser().getIdentifier(),
					this.userAPI.getUser().getLocale());

			// Storage API enables the App to store data persistently
			// whatever is stored via the storage API will also be available if
			// the App is restarted
			this.storageAPI = context.getAPI(StorageAPI.class);
			initializeStorage();

			// Frontend API enables the App to expose a user interface
			this.frontendAPI = context.getAPI(FrontendAPI.class);
			initializeWebResources();

			// Environment API gives a access for rooms, current situation etc.
			this.environmentAPI = context.getAPI(EnvironmentAPI.class);
			initializeEnvironment();

			// Device API gives access to devices connected to IOLITE
			this.deviceAPI = context.getAPI(DeviceAPI.class);
			initializeDeviceManager();

			// getting IOLTE user-ID
			final UserAPI userApi;
			final String userId;
			try {
				userApi = context.getAPI(UserAPI.class);
				userId = userApi.getUser().getIdentifier();
			} catch (final IOLITEAPINotResolvableException | IOLITEPermissionDeniedException e) {
				LOGGER.error(MSG_ERR_RETRIEVE_USERAPI, e);
				throw new StartFailedException(MSG_ERR_RETRIEVE_USERAPI, e);
			}
			/** Register different Views for Mirror */
			final ResourcePackageConfig staticResourceConfig = new ResourcePackageConfig(VIEW_RESOURCES);
			staticResourceConfig.addView(VIEW_ID_CLOCK, VIEW_RESPATH_CLOCK, ICON_RESPATH_CLOCK);
			staticResourceConfig.addView(VIEW_ID_TRAFFIC, VIEW_RESPATH_TRAFFIC, ICON_RESPATH_TRAFFIC);
			staticResourceConfig.addView(VIEW_ID_WEATHER, VIEW_RESPATH_WEATHER, ICON_RESPATH_WEATHER);
			staticResourceConfig.addView(VIEW_ID_CALENDAR, VIEW_RESPATH_CALENDAR, ICON_RESPATH_CALENDAR);
			this.viewRegistrator = new ViewRegistrator(staticResourceConfig, APP_ID, userId);
			deviceAPI.setObserver(this.viewRegistrator);
			deviceAPI.getDevices().forEach(this.viewRegistrator::addedToDevices);

			/** scheduler that updates the calendar information every 15 min */
			this.calendarUpdateThread = scheduler.scheduleAtFixedRate(() -> {


				try {

					/**
					 * Update storage API and based on user interface settings
					 * update device functionalities
					 */

					this.storageAPI = context.getAPI(StorageAPI.class);
					this.environmentAPI = context.getAPI(EnvironmentAPI.class);

					boolean mirror = "true".equals(getStringorDefault("Mirror", "false"));
					boolean weather = "true".equals(getStringorDefault("Mirror_Weather", "false"));
					boolean clock = "true".equals(getStringorDefault("Mirror_Clock", "false"));
					boolean calendarBool = "true".equals(getStringorDefault("Mirror_Calendar", "false"));
					boolean traffic = "true".equals(getStringorDefault("Mirror_Traffic", "false"));
					boolean sonos = "true".equals(getStringorDefault("Sonos", "false"));
					String sonosURI = getStringorDefault("SonosURI",
							"http://downloads.hendrik-motza.de/Annoying_Alarm_Clock.mp3");
					boolean controlPanel = "true".equals(getStringorDefault("ControlPanel", "false"));

					sonosActive(sonos);
					mirrorActive(mirror, weather, clock, calendarBool, traffic);

				} catch (final MirrorApiException e) {
					LOGGER.error("MirrorApiException", e);
				} catch (GeneralSecurityException e) {
					LOGGER.error("GeneralSecurityException", e);
				} catch (URISyntaxException e) {
					LOGGER.error("URISyntaxException", e);
				} catch (StorageAPIException e) {
					LOGGER.error("StorageAPIException", e);
				} catch (IOLITEAPINotResolvableException e) {
					LOGGER.error("IOLITEAPINotResolvableException", e);
				} catch (IOLITEPermissionDeniedException e) {
					LOGGER.error("IOLITEPermissionDeniedException", e);
				} catch (IOException e) {
					LOGGER.error("IOException!", e);
				} catch (ParseException e) {
					LOGGER.error("ParseException!", e);
				} catch (IllegalStateException e) {
					LOGGER.error("IllegalStateException!", e);
				}

			// change the update time here 
			}, 0, 1, TimeUnit.MINUTES);

			LOGGER.debug("Mirror Views got registered!");

		} catch (final IOLITEAPINotResolvableException e) {
			throw new StartFailedException(
					MessageFormat.format("Start failed due to required but not resolvable AppAPI: {0}", e.getMessage()),
					e);
		} catch (final IOLITEPermissionDeniedException e) {
			throw new StartFailedException(MessageFormat
					.format("Start failed due to permission denied problems in the examples: {0}", e.getMessage()), e);
		} catch (final StorageAPIException | FrontendAPIException e) {
			throw new StartFailedException(
					MessageFormat.format("Start failed due to an error in the App API examples: {0}", e.getMessage()),
					e);
		}

		LOGGER.debug("Started");
	}

	/**
	 * sonos is true if sonos box has been enabled by the user in the app interface. latest calendar
	 * information is send to sonos controller	 * 
	 * @param sonos	 * 
	 * @throws IOException
	 * @throws ParseException
	 * @throws GeneralSecurityException
	 * @throws URISyntaxException
	 */
	private void sonosActive(boolean sonos)
			throws IOException, ParseException, GeneralSecurityException, URISyntaxException {

		if (sonos) {
			GoogleData calendar_data = new GoogleData();
			this.calendar = calendar_data.getData();
			this.sonosController.playSongAt(this.calendar, this.storageController);
		}
	}

	/**
	 * input parameters are true if views are enabled by the user in the user interface of the app. latest
	 * calendar information is send to views.
	 * 
	 * @param mirror
	 * @param weather
	 * @param clock
	 * @param calendarBool
	 * @param traffic
	 * @throws StorageAPIException
	 * @throws IOException
	 * @throws ParseException
	 *
	 * @throws GeneralSecurityException
	 * @throws URISyntaxException
	 */
	private void mirrorActive(boolean mirror, boolean weather, boolean clock, boolean calendarBool, boolean traffic)
			throws StorageAPIException, IOException, ParseException, GeneralSecurityException, URISyntaxException {
		if (mirror) {

			if (calendarBool) {

				getCalendar();
				LOGGER.warn(CalendarIntegrationAppMain.this.calendar.toString());
				final TemplateConfig templateConf_calendar = new TemplateConfig(VIEW_TEMPLATE_CALENDAR,
						VIEW_WEBPATH_CALENDAR, VIEW_ID_CALENDAR);
				templateConf_calendar.putReplacement("{CALENDAR}", CalendarIntegrationAppMain.this.calendar.toString());
				CalendarIntegrationAppMain.this.viewRegistrator.updateTemplatePage(templateConf_calendar);
			} // calendar is active

			else {
				final TemplateConfig templateConf_calendar = new TemplateConfig(VIEW_TEMPLATE_CALENDAR,
						VIEW_WEBPATH_CALENDAR, VIEW_ID_CALENDAR);
				templateConf_calendar.putReplacement("{CALENDAR}", "&nbsp;");
				CalendarIntegrationAppMain.this.viewRegistrator.updateTemplatePage(templateConf_calendar);

			}

			if (weather) {

				final TemplateConfig templateConf_weather = new TemplateConfig(VIEW_RESPATH_WEATHER,
						VIEW_WEBPATH_WEATHER, VIEW_ID_WEATHER);
				refreshWeather();
				templateConf_weather.putReplacement("{WEATHER}", this.temperature + " &deg C");
				CalendarIntegrationAppMain.this.viewRegistrator.updateTemplatePage(templateConf_weather);
			} // weather is active
			else {
				final TemplateConfig templateConf_weather = new TemplateConfig(VIEW_RESPATH_WEATHER,
						VIEW_WEBPATH_WEATHER, VIEW_ID_WEATHER);
				templateConf_weather.putReplacement("{WEATHER}", "&nbsp;");
				CalendarIntegrationAppMain.this.viewRegistrator.updateTemplatePage(templateConf_weather);
			}
			if (clock) {
				final TemplateConfig templateConf_clock = new TemplateConfig(VIEW_RESPATH_CLOCK, VIEW_WEBPATH_CLOCK,
						VIEW_ID_CLOCK);
				templateConf_clock.putReplacement("&nbsp;", "<script src='js/datetime.js'></script>");
				CalendarIntegrationAppMain.this.viewRegistrator.updateTemplatePage(templateConf_clock);
			} // clock is active
			if (!clock) {
				final TemplateConfig templateConf_clock = new TemplateConfig(VIEW_RESPATH_CLOCK, VIEW_WEBPATH_CLOCK,
						VIEW_ID_CLOCK);
				templateConf_clock.putReplacement("<script src='js/datetime.js'></script>", "&nbsp;");
				CalendarIntegrationAppMain.this.viewRegistrator.updateTemplatePage(templateConf_clock);
			}

			if (traffic) {
				trafficActive();
			} // traffic is active
			if (!traffic) {
				final TemplateConfig templateConf_traffic = new TemplateConfig(VIEW_RESPATH_EMPTY_TRAFFIC,
						VIEW_WEBPATH_TRAFFIC, VIEW_ID_TRAFFIC);
				templateConf_traffic.putReplacement("{TRAFFIC}", "&nbsp;");
				CalendarIntegrationAppMain.this.viewRegistrator.updateTemplatePage(templateConf_traffic);
			}

			
		} else {
			final TemplateConfig templateConf_calendar = new TemplateConfig(VIEW_TEMPLATE_CALENDAR,
					VIEW_WEBPATH_CALENDAR, VIEW_ID_CALENDAR);
			templateConf_calendar.putReplacement("{CALENDAR}", "&nbsp;");
			CalendarIntegrationAppMain.this.viewRegistrator.updateTemplatePage(templateConf_calendar);

			final TemplateConfig templateConf_clock = new TemplateConfig(VIEW_RESPATH_CLOCK, VIEW_WEBPATH_CLOCK,
					VIEW_ID_CLOCK);
			templateConf_clock.putReplacement("<script src='js/datetime.js'></script>", "&nbsp;");
			CalendarIntegrationAppMain.this.viewRegistrator.updateTemplatePage(templateConf_clock);

			final TemplateConfig templateConf_weather = new TemplateConfig(VIEW_RESPATH_WEATHER, VIEW_WEBPATH_WEATHER,
					VIEW_ID_WEATHER);
			templateConf_weather.putReplacement("{WEATHER}", "&nbsp;");
			CalendarIntegrationAppMain.this.viewRegistrator.updateTemplatePage(templateConf_weather);

			final TemplateConfig templateConf_traffic = new TemplateConfig(VIEW_RESPATH_EMPTY_TRAFFIC,
					VIEW_WEBPATH_TRAFFIC, VIEW_ID_TRAFFIC);
			templateConf_traffic.putReplacement("{TRAFFIC}", "&nbsp;");
			CalendarIntegrationAppMain.this.viewRegistrator.updateTemplatePage(templateConf_traffic);

		}

	}

	/**
	 * method to find the next upcoming event location
	 */
	private void trafficActive() {

		if (this.calendar.getTodayEvents().isEmpty() || this.calendar.getTodayEvents() == null){
			
			final TemplateConfig templateConf_traffic = new TemplateConfig(VIEW_RESPATH_NO_APPOINTMENT_TRAFFIC,
					VIEW_WEBPATH_TRAFFIC, VIEW_ID_TRAFFIC);
			templateConf_traffic.putReplacement("{TRAFFIC}", "No traffic information.");
			CalendarIntegrationAppMain.this.viewRegistrator.updateTemplatePage(templateConf_traffic);	
		}
		else {

			for (int i = 0; i < this.calendar.getTodayEvents().size(); i++) {
				
				
				long time = this.calendar.getTodayEvents().get(i).getBegin().getTimeInMillis() - System.currentTimeMillis();
				
				if (time > 0) {
					if (!this.calendar.getTodayEvents().get(i).getLocation().equals("Unknown Location") && this.calendar.getTodayEvents().get(i).getLocation().contains(",") ) {
						final TemplateConfig templateConf_traffic = new TemplateConfig(VIEW_RESPATH_TRAFFIC, VIEW_WEBPATH_TRAFFIC,
								VIEW_ID_TRAFFIC);
						templateConf_traffic.putReplacement("{TRAFFIC}", this.calendar.getTodayEvents().get(i).getLocation());	
						CalendarIntegrationAppMain.this.viewRegistrator.updateTemplatePage(templateConf_traffic);
						break;
					} // unknown location
					else {
						final TemplateConfig templateConf_traffic = new TemplateConfig(VIEW_RESPATH_EMPTY_TRAFFIC,
								VIEW_WEBPATH_TRAFFIC, VIEW_ID_TRAFFIC);
						templateConf_traffic.putReplacement("{TRAFFIC}", "The Location has not been clearly specified by the user.");
						CalendarIntegrationAppMain.this.viewRegistrator.updateTemplatePage(templateConf_traffic);	
						break;
					} // else
				} // if 
			}
			
		}
		
		
	}

	/**
	 * get latest calendar data and sort out events by types sport, friends,
	 * university and other. In case not all views are required this method
	 * reduces the events
	 * 
	 * @throws IOException
	 * @throws GeneralSecurityException
	 * @throws URISyntaxException
	 * @throws StorageAPIException
	 */
	private void getCalendar()
			throws IOException, ParseException, GeneralSecurityException, URISyntaxException, StorageAPIException {
		GoogleData calendar_data = new GoogleData();
		DailyEvents fulldata = calendar_data.getData();

		boolean sport = "true".equals(getStringorDefault("Sport", "false"));
		boolean friend = "true".equals(getStringorDefault("Friend", "false"));
		boolean uni = "true".equals(getStringorDefault("University", "false"));
		boolean other = "true".equals(getStringorDefault("Other", "false"));

		CalendarIntegrationAppMain.this.calendar = fulldata.sortOut(sport, friend, uni, other);

	}

	/**
	 * Updates the weather information for calendar scheduler
	 * 
	 * @return a String containing the current temperature
	 */
	private void refreshWeather() {
		for (final Device device : this.deviceAPI.getDevices()) {

			if (device.getProfileIdentifier().equals(DriverConstants.PROFILE_WeatherStation_ID)) {

				LOGGER.info("Update Weather");

				DeviceDoubleProperty temp = device
						.getDoubleProperty(DriverConstants.PROPERTY_outsideEnvironmentTemperature_ID);

				if (temp != null && temp.getValue().toString() != null) {
					LOGGER.warn("The current temperature is:  '{}'", temp.getValue());
					this.temperature = temp.getValue().toString();
				}
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void stopHook() throws StopFailedException {
		LOGGER.debug("Stopping");

		// deregister the static assets
		if (this.disposeableAssets != null) {
			this.disposeableAssets.dispose();
		}
		this.calendarUpdateThread.cancel(false);
		LOGGER.debug("Stopped");
	}

	private void initializeEnvironment(){
		this.environmentController = new EnvironmentController(this.environmentAPI);
	}
	/**
	 * Example method showing how to use the Device API.
	 * @throws URISyntaxException 
	 * @throws GeneralSecurityException 
	 * @throws ParseException 
	 * @throws IOException 
	 */

	private void initializeDeviceManager() {

		this.deviceAPI.setObserver(new DeviceAddAndRemoveLogger());
		

		// Find Driver of the Sonos Box and make Settings in the sonosController
		// Class
		for (final Device device : this.deviceAPI.getDevices()) {

			LOGGER.debug(device.getIdentifier());

			if (device.getIdentifier().equals("RINCON_B8E9373AD10E01400")) {
				this.sonosController.setSonos(device, this.scheduler, this.environmentController,
						calendar, this.storageController);
				LOGGER.debug("Configured SONOS controller for device '{}'", device.getIdentifier());
			}
			 String ID = device.getIdentifier();
			 
			 LOGGER.warn("Die ID ist: " + ID);
             
             if (ID != null){
                 if (ID.equals("knx_kitchen_lcd0")){
                	 // Find the display device and it's property 
                     
                     DeviceStringProperty displayProperty = device.getStringProperty(DriverConstants.PROPERTY_mediaTitle_ID);
                     
                     if (displayProperty != null){
                         List<String> messagesToDisplay = GoogleEventProcessor.getUpcomingEventMessages();
							new ScrollingPublisher().pushMessages(displayProperty, messagesToDisplay);
							displayProperty.setObserver(new DeviceStringPropertyObserver(){

							    @Override
							    public void deviceChanged(Device arg0) {
							        // TODO Auto-generated method stub
							        
							    }

							    @Override
							    public void keyChanged(String arg0) {
							        // TODO Auto-generated method stub
							        
							    }

							    @Override
							    public void valueChanged(String arg0) {
							        LOGGER.info("Changed TITLE!!!");
							        
							    }
							    
							});
                     }
             }else{
               
             }
             }
		}
	

	}

	/**
	 * Loading Checkbox values from StorageAPI
	 *
	 * @throws StorageAPIException
	 */
	private void initializeStorage() throws StorageAPIException {

		this.storageController = new StorageController(this.storageAPI);

		try {
			LOGGER.debug("loading 'mirror' from storage: {}", String.valueOf(this.storageAPI.loadString("Mirror")));
			LOGGER.debug("loading 'control_panel' from storage: {}",
					String.valueOf(this.storageAPI.loadString("ControlPanel")));
			LOGGER.debug("loading 'sonos' from storage: {}", String.valueOf(this.storageAPI.loadString("Sonos")));
			LOGGER.debug("loading 'clock' from storage: {}",
					String.valueOf(this.storageAPI.loadString("Mirror_Clock")));
			LOGGER.debug("loading 'weather' from storage: {}",
					String.valueOf(this.storageAPI.loadString("Mirror_Weather")));
			LOGGER.debug("loading 'traffic' from storage: {}",
					String.valueOf(this.storageAPI.loadString("Mirror_Traffic")));
			LOGGER.debug("loading 'calendar' from storage: {}",
					String.valueOf(this.storageAPI.loadString("Mirror_Calendar")));
			LOGGER.debug("loading 'sport_appointments' from storage: {}",
					String.valueOf(this.storageAPI.loadString("Sport")));
			LOGGER.debug("loading 'friend_appointments' from storage: {}",
					String.valueOf(this.storageAPI.loadString("Friend")));
			LOGGER.debug("loading 'university_appointments' from storage: {}",
					String.valueOf(this.storageAPI.loadString("University")));
			LOGGER.debug("loading 'other_appointments' from storage: {}",
					String.valueOf(this.storageAPI.loadString("Other")));
			LOGGER.debug("loading 'sonosURI' from storage: {}", String.valueOf(this.storageAPI.loadString("SonosURI")));
		} catch (StorageAPIException e) {
			LOGGER.debug("Failed loading of StorageAPI");
		}

	}

	/**
	 * Read out storage value or set default value in case no value is stored
	 * yet
	 * 
	 * @param key
	 * @param defaultvalue
	 * @return
	 */
	public String getStringorDefault(String key, String defaultvalue) {
		try {
			return this.storageAPI.loadString(key);
		} catch (StorageAPIException e) {
			try {
				this.storageAPI.saveString(key, defaultvalue);
			} catch (StorageAPIException e1) {
				LOGGER.error("Can't write value for key", e1);
			}
			return defaultvalue;
		}
	}

	/**
	 * Registering web resources.
	 *
	 * @throws FrontendAPIException
	 *             if some resources are not found.
	 */
	private final void initializeWebResources() throws FrontendAPIException {

		// go through static assets and register them
		final Map<URI, PathHandlerPair> assets = StaticResources.scanClasspath("assets", getClass().getClassLoader());
		this.disposeableAssets = FrontendAPIUtility.registerPublicHandlers(this.frontendAPI, assets);

		// index page
		final IOLITEHTTPRequestHandler indexPageRequestHandler = new PageWithEmbeddedSessionTokenRequestHandler(
				loadTemplate("assets/index.html"));
		this.frontendAPI.registerRequestHandler("", indexPageRequestHandler);
		this.frontendAPI.registerRequestHandler("index.html", indexPageRequestHandler);

	}

	/**
	 * Load a HTML template as string.
	 */
	private String loadTemplate(final String templateResource) {
		try {
			return StaticResources.loadResource(templateResource, getClass().getClassLoader());
		} catch (final IOException e) {
			throw new InitializeFailedException("Loading templates for the calendar app failed", e);
		}
	}

}