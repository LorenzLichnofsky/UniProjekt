package de.iolite.apps.example;

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
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
//TODO add source 
public class Quickstart {
    /** Application name. */
    private static final String APPLICATION_NAME =
        "Google Calendar API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/calendar-java-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/calendar-java-quickstart
     */
    private static final List<String> SCOPES =
        Arrays.asList(CalendarScopes.CALENDAR_READONLY);

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
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
            Quickstart.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Calendar client service.
     * @return an authorized Calendar client service
     * @throws IOException
     */
    public static com.google.api.services.calendar.Calendar
        getCalendarService() throws IOException {
        Credential credential = authorize();
        return new com.google.api.services.calendar.Calendar.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void main(String[] args) throws IOException, ParseException {
        // Build a new authorized API client service.
        // Note: Do not confuse this class with the
        //   com.google.api.services.calendar.model.Calendar class.
        com.google.api.services.calendar.Calendar service =
            getCalendarService();

        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        // create a maxTime today 23:59:59
        Date day = new Date(System.currentTimeMillis());
        //TODO methods old
        day.setHours(23);
        day.setMinutes(59);
        day.setSeconds(59);
        DateTime t = new DateTime(day);
        
        Events events = service.events().list("primary")
        	.setMaxResults(20)
        	// just upcoming Events
            .setTimeMin(now)
            // just today's Events
            .setTimeMax(t)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute();
        
        List<Event> items = events.getItems();
     // Retrieve color definitions for calendars and events
       List<GoogleEvent> allToday = new LinkedList<GoogleEvent>();
     // Print available event colors
       
             for (Event event : items) {
            	GoogleEvent today = new GoogleEvent();
                DateTime start = event.getStart().getDateTime();                              
                if (start != null) {
                today.setBegin(start); 	
                }
                DateTime end = event.getEnd().getDateTime();                              
                if (end != null) {
                today.setEnd(end); 	
                }
                String Name = event.getDescription();
                if (Name != null) today.setName(Name);
                String color = event.getColorId();
                String status = "start";
                if (color != null ){
                	switch(color){
                	case "1": status = "work";
                			break;
                	case "2": status = "private";
                	        break;
                	case "3": status = "task";
                			break;
                	case "4": status = "iwie";
                			break;
                	case "5": status = "iwas";
                            break;               	
                	case "6": status = "iwo";
                			break;
                	case "7": status = "iwann";
                		break;
                	case "8": status = "iwall";
                		break;
                	case "9": status = "iwass";
                		break;
                	case "10":status = "iwamm";
                		break;
                	case "11": status = "iwajj";
                		break;
                	default: status = "So geht es nicht";
                	}	
                }
                today.setStatus(status);
                System.out.println(status);
                String kind = event.getKind();
                if (kind != null) today.setKind(kind);
                
                String location = event.getLocation();;
                if (location != null) today.setLocation(location);
                
                allToday.add(today);
                //event.getCreator();
                System.out.println(today.toString());
               
            }
        
    }

}