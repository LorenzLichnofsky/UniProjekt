package de.iolite.apps.example;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.iolite.data.GoogleData;
import de.iolite.data.GoogleEvent;

public class GoogleEventProcessor {

	/**
	 * Connects to google calendar making use of a GoogleData object and requests the events for the current day. Then, it
	 * selects the upcoming event, and builds a list of strings with the event's name, beginning time, end time and location
	 */
	public static List<String> getUpcomingEventMessages() {
		List<String> results = new ArrayList<>();
		List<GoogleEvent> todayEvents;
		try {
			todayEvents = new GoogleData().getData().getTodayEvents();
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong", e);
		}
		GoogleEvent upcoming = null;
		Calendar now = Calendar.getInstance();
		if (!todayEvents.isEmpty()) {
			upcoming = todayEvents.get(0);
		}
		for (GoogleEvent ge : todayEvents) {
			if (ge.getBegin().after(now) && ge.getBegin().before(upcoming)) {
				upcoming = ge;
			}
		}
		if (upcoming == null) {
			results.add("No events");
		} else {
			results.add(upcoming.getName());
			results.add(upcoming.getBegin().toString());
			results.add(upcoming.getEnd().toString());
			results.add(upcoming.getLocation());
		}
		return results;
	}
}
