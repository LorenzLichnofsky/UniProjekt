package de.iolite.apps.example;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Source mainly taken from
 * https://developers.google.com/google-apps/calendar/quickstart/java
 * additional:
 */

public class Quickstart2 {
	/** Application name. */
	private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(
			System.getProperty("user.home"),
			".credentials/calendar-java-quickstart");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/**
	 * Global instance of the scopes required by this quickstart.
	 *
	 * If modifying these scopes, delete your previously saved credentials at
	 * ~/.credentials/calendar-java-quickstart
	 */
	private static final List<String> SCOPES = Arrays
			.asList(CalendarScopes.CALENDAR_READONLY);

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
	 * Build and return an authorized Calendar client service.
	 * 
	 * @return an authorized Calendar client service
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static com.google.api.services.calendar.Calendar getCalendarService()
			throws IOException, GeneralSecurityException {
		GoogleCredential credential = new GoogleCredential.Builder()
				.setTransport(HTTP_TRANSPORT)
				.setJsonFactory(JSON_FACTORY)
				.setServiceAccountId(
						"calendarapp@massive-triumph-150916.iam.gserviceaccount.com")

				.setServiceAccountPrivateKeyFromP12File(
						new File("src/main/resources/mycalendarproject.p12"))
				.setServiceAccountScopes(SCOPES)
				// .setServiceAccountScopes(Collections.singleton(SQLAdminScopes.SQLSERVICE_ADMIN))
				.setServiceAccountUser("arianeziehn@googlemail.com")

				.build();

		// Credential credential = authorize();

		return new com.google.api.services.calendar.Calendar.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
				APPLICATION_NAME).build();
	}

	public static void main(String[] args) throws IOException, ParseException,
			GeneralSecurityException {
		// Build a new authorized API client service.
		// Note: Do not confuse this class with the
		// com.google.api.services.calendar.model.Calendar class.
		com.google.api.services.calendar.Calendar service = getCalendarService();

		// create the current dateTime
		com.google.api.client.util.DateTime now = new com.google.api.client.util.DateTime(
				System.currentTimeMillis());
		// create a maxTime today 23:59:59 as we just need daily information
		Date day = new Date(System.currentTimeMillis());
		// methods old but needed for google.api.client
		day.setHours(23);
		day.setMinutes(59);
		day.setSeconds(59);
		com.google.api.client.util.DateTime t = new com.google.api.client.util.DateTime(
				day);

		Events events = service.events().list("primary")
		// .setMaxResults(20)
		// just upcoming Events
				.setTimeMin(now)
				// just today's Events
				// .setTimeMax(t)
				.setOrderBy("startTime").setSingleEvents(true).execute();

		List<Event> items = events.getItems();
		List<GoogleEvent> allToday = new LinkedList<GoogleEvent>();

		for (com.google.api.services.calendar.model.Event event : items) {
			GoogleEvent today = new GoogleEvent();

			com.google.api.client.util.DateTime start = event.getStart()
					.getDateTime();
			com.google.api.client.util.DateTime end = event.getEnd()
					.getDateTime();

			// Date startDate = start.toDate();
			// ohne Zeitangabe kein Event
			if (start != null && end != null) {

				java.util.Calendar cStart = java.util.Calendar.getInstance();
				cStart.setTimeInMillis(start.getValue());
				java.util.Calendar cEnd = java.util.Calendar.getInstance();
				cEnd.setTimeInMillis(end.getValue());

				today.setBegin(cStart);
				today.setEnd(cEnd);

				// DateTime end = event.getEnd().getDateTime();
				// if (end != null) {
				// today.setEnd(end);
				// }
				String Name = event.getSummary();
				if (Name != null)
					today.setName(Name);
				else
					today.setName("Your Appointment");

				String color = event.getColorId();

				String status = "nA";
				if (color != null) {
					switch (color) {
					case "1":
						status = "work";
						break;

					case "2":
						status = "uiiiii";
						break;
					case "3":
						status = "task";
						break;
					case "4":
						status = "dunkelesorange";
						break;
					case "5":
						status = "gelb";
						break;
					case "6":
						status = "orange";
						break;
					case "7":
						status = "iwann";
						break;
					case "8":
						status = "iwall";
						break;
					case "9":
						status = "blau";
						break;
					case "10":
						status = "grün";
						break;
					// rot
					case "11":
						status = "red";
						break;
					default:
						status = "So geht es nicht";
					}
				}
				// TODO default for null value
				if (color == null)
					status = "weiß";

				today.setColor(status);

				// String kind = event.getKind();
				// if (kind != null) today.setKind(kind);

				String location = event.getLocation();
				;
				if (location != null)
					today.setLocation(location);
				else
					today.setLocation("unkown");

				String share = event.getVisibility();
				if (share != null)
					today.setStatus(share);
				else
					today.setStatus("private");

				// System.out.println(event..getVisibility());

				allToday.add(today);
				// event.getCreator();
				DailyEvents todayFinal = new DailyEvents(allToday);

			}
		}
	}

}