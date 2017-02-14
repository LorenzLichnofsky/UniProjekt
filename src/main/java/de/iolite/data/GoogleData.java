package de.iolite.data;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import com.google.api.services.calendar.model.Event.Reminders;
import de.iolite.apps.example.devices.SonosController;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * This class extracts the DAILY Event Data from one Google Calendar.
 * Instructions and source code source:
 * https://developers.google.com/google-apps/calendar/quickstart/java
 * 
 * @author Ariane Ziehn, Alia Siemund
 * @version 1.0
 * @see DailyEvents
 * @see GoogleEvent
 */
public class GoogleData {
	
	/** Application name. */
	private static final String APPLICATION_NAME = "Calendar APP";

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(
			System.getProperty("user.home"), ".credentials/googlecalendar");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/** Global instance of the required scopes */
	private static final List<String> SCOPES = Arrays
			.asList(CalendarScopes.CALENDAR_READONLY);
	
	/** Instance of Sonos Controller */
	SonosController controller;

	/** Initializing HTTP_TRANSPORT and DATA_STORE_FACTORY */
	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}
	
	
	/**
	 * Creates an authorized Credential object.
	 * 
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public static Credential authorize() throws IOException {
		// Load client secrets from client_secret.json file
		InputStream in = GoogleData.class
				.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
				JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(DATA_STORE_FACTORY)
				.setAccessType("offline").build();
		Credential credential = new AuthorizationCodeInstalledApp(flow,
				new LocalServerReceiver()).authorize("user");
		return credential;
	}

	/**
	 * Build and return an authorized Calendar client service.
	 * 
	 * @return an authorized Calendar client service
	 * @throws IOException
	 */
	public static com.google.api.services.calendar.Calendar getCalendarService()
			throws IOException {
		Credential credential = authorize();
		return new com.google.api.services.calendar.Calendar.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
				APPLICATION_NAME).build();
	}

	/**
	 * Creates a list of all events taking place today including the information
	 * from GoogleCalendar.
	 * 
	 * @return list of today's events
	 * @throws IOException
	 * @throws ParseException
	 * @throws GeneralSecurityException
	 * @throws URISyntaxException
	 */
	public DailyEvents getData() throws IOException, ParseException,
			GeneralSecurityException, URISyntaxException {

		// Build a new authorized API client service.
		com.google.api.services.calendar.Calendar service = getCalendarService();

		// create the current dateTime (no events before this time are loaded)
		com.google.api.client.util.DateTime now = new com.google.api.client.util.DateTime(
				System.currentTimeMillis());
		// create a maximal dateTime (23:59:59) to exclude all events after
		// today
		Date day = new Date(System.currentTimeMillis());
		// methods are deprecated but needed for google.api.client
		day.setHours(23);
		day.setMinutes(59);
		day.setSeconds(59);
		
		com.google.api.client.util.DateTime latest = new com.google.api.client.util.DateTime(
				day);
		// get all events of today from service
		Events events = service.events().list("primary").setMaxResults(12)
				.setTimeMin(now)
				.setTimeMax(latest)
				.setOrderBy("startTime").setSingleEvents(true).execute();

		List<Event> items = events.getItems();
		// the final List of GoogleEvents used for the App
		List<GoogleEvent> allToday = new LinkedList<GoogleEvent>();

		// Read out and Adjustment of GoogleEvents
		for (com.google.api.services.calendar.model.Event event : items) {

			// get start and end time of the event
			com.google.api.client.util.DateTime start = event.getStart()
					.getDateTime();
			com.google.api.client.util.DateTime end = event.getEnd()
					.getDateTime();

			// without start and end time no GoogleEvent is created
			if (start != null && end != null) {
				GoogleEvent today = new GoogleEvent();
				java.util.Calendar cStart = java.util.Calendar.getInstance();
				cStart.setTimeInMillis(start.getValue());
				java.util.Calendar cEnd = java.util.Calendar.getInstance();
				cEnd.setTimeInMillis(end.getValue());
			
				today.setBegin(cStart);
				today.setEnd(cEnd);

				// get the name of the appointment
				String Name = event.getSummary();
				if (Name != null)
					today.setName(Name);
				else
					today.setName("Your Appointment");

				// get the color of the event to define the event type
				String color = event.getColorId();
				if (color == null){
					color = "7";					
				}

				String status = "";

				if (color != null) {
					switch (color) {
					case "5":
						status = "Friend";
						break;

					case "10":
						status = "Sport";
						break;
					case "11":
						status = "University";
						break;
					default:
						status = "Other";
					}
				}

				

				today.setColor(status);

				// Find all reminders set by the user
				Reminders reminders = event.getReminders();
				List<EventReminder> overrideReminders = new ArrayList<EventReminder>();
				List<java.util.Calendar> notifications = new ArrayList<java.util.Calendar>();
				overrideReminders = reminders.getOverrides();
				// if no reminder is set for google Start the user will be reminded one hour before the appointment
				if(overrideReminders == null){
				long timeWarning1 = event.getStart().getDateTime().getValue()-(60*60000);
				java.util.Calendar time1 = java.util.Calendar.getInstance();
				time1.setTimeInMillis(timeWarning1);
				notifications.add(time1);
				}
				else{
					
					for (EventReminder rem : overrideReminders) {
						// 1 minute = 60.000 milliseconds and long value is in milliseconds
						long timeWarning2 = event.getStart().getDateTime().getValue()-rem.getMinutes()*60000;
						java.util.Calendar time2 = java.util.Calendar.getInstance();
						time2.setTimeInMillis(timeWarning2);
						notifications.add(time2);
					}// for each reminder
					
				}
				today.setNotifications(notifications);
				// get the location of the appointment
				String location = event.getLocation();

				if (location != null)
					today.setLocation(location);

				else
					today.setLocation("Unknown Location");

				// get the people adding the appointment
				List<EventAttendee> share = event.getAttendees();
				if (share != null) {
					List<String> attendee = new ArrayList<String>();
					for (EventAttendee person : share) {
						if (person.getDisplayName() != null)
							attendee.add(person.getDisplayName());
					}// for
					if(attendee.isEmpty()) attendee.add("NOBODY");
					today.setAttendee(attendee);
				} else {
					
					/**
					 * do something else. Attendee in general not used by our group
					 */
					
				} // if

				allToday.add(today);

			}// if time exits
		}// for loop each event

		// create final list of today's events usable for devices
		DailyEvents todayFinal = new DailyEvents();
		if(allToday != null){
		todayFinal = new DailyEvents(allToday);
		}
		
		return todayFinal;
		

	}
}
