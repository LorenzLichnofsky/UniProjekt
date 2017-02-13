package de.iolite.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/** 
 * This class stores all the DAILY events and their attributes.
 * @author Ariane Ziehn
 * 
 */
public class DailyEvents {

	List<GoogleEvent> todayEvents = new LinkedList<GoogleEvent>();

	public DailyEvents(List<GoogleEvent> list) {
		this.todayEvents = list;
	}

	public DailyEvents() {

	}

	public List<GoogleEvent> getTodayEvents() {
		return todayEvents;
	}

	public void setTodayEvents(List<GoogleEvent> todayEvents) {
		this.todayEvents = todayEvents;
	}

	private static String [][] UMLAUT_REPLACEMENT = { 
			{new String("Ä"),"&Auml;" }, 
			{new String("Ö"),"&Ouml;" }, 
			{new String("Ü"),"&Uuml;" },
			{new String("ä"),"&auml;" },
			{new String("ö"),"&ouml;" },
			{new String("ü"),"&uuml;" },
			{new String("ß"),"&szlig;" }
	};
	
	/** 
	 * Replaces all german Umlaute by responding HTML name. 
	 * 
	 */
	public static String replaceUmlaute (String orig){
		String result = orig;
		
		for(int i = 0; i<UMLAUT_REPLACEMENT.length; i++) {
			result = result.replaceAll(UMLAUT_REPLACEMENT[i][0], UMLAUT_REPLACEMENT[i][1]);
		}
		
		return result;
	}
			
			
	/**
	 * Calendar Content displayed on the Mirror	
	 */
	@Override
	public String toString() {

		String event = "";
		if (!todayEvents.isEmpty()) {
			for (int i = 0; i < todayEvents.size(); i++) {
				GoogleEvent dummy = todayEvents.get(i);
				int minutes = dummy.end.get(Calendar.MINUTE);
				int minutesStart = dummy.begin.get(Calendar.MINUTE);
				String ende = "";
				String start = "";
				String shortlocation = dummy.Location.split(",")[0]; //cut postal code, city and country to keep location short
				if (minutes < 10) {
					ende = ":0" + minutes;
				} else
					ende = ":" + minutes;

				if (minutesStart < 10) {
					start = ":0" + minutesStart;
				} else
					start = ":" + minutesStart;

				event += replaceUmlaute(dummy.Name) + "<br/>"
						+ replaceUmlaute(shortlocation)
						+ "<br/> " + dummy.begin.get(Calendar.HOUR_OF_DAY) + start
						+ " - " + dummy.end.get(Calendar.HOUR_OF_DAY) + ende;
						
	
						event += "<br/>" + "<br/>";


			}
			return event;
		} else {
			return event + "today no upcoming events";
		}
	}

	/**
	 * sort out the different event types chosen by the user. 
	 * @param sport
	 * @param friend
	 * @param uni
	 * @param other
	 * @return only google events selected by user
	 */
	public DailyEvents sortOut(boolean sport, boolean friend, boolean uni, boolean other) {
		
		List<GoogleEvent> todayEventsSorted = new ArrayList<GoogleEvent>();
		
		if(sport && friend && uni && other)
			todayEventsSorted = this.todayEvents;
		
		else{
		for (int i = 0; i<this.todayEvents.size(); i++){
			GoogleEvent g = this.todayEvents.get(i);
			if(g.Color=="Friend" && friend)
				todayEventsSorted.add(g);
			if(g.Color=="Sport" && sport)
				todayEventsSorted.add(g);
			if(g.Color=="University" && uni)
				todayEventsSorted.add(g);
			if(g.Color=="Other" && other)
				todayEventsSorted.add(g);
		}
		}
		
		List<GoogleEvent> todayEventsReduced = new ArrayList<>();
		
		if (todayEventsSorted.size() >= 3 ){
			todayEventsReduced = todayEventsSorted.subList(0, 3);	
		} else {
			todayEventsReduced = todayEventsSorted;
		}
	
		
		return new DailyEvents(todayEventsReduced);
	}

	/**
	 * Sonos searching for the exact time of the reminder of all daily events 
	 * @return List of all reminders
	 */
	public LinkedList<Date> getAlarm (){
    	
		LinkedList <Date> reminders = new LinkedList<Date>();
		
    		if (!todayEvents.isEmpty()) {
            
    			for (int i = 0; i < todayEvents.size(); i++) {
               	 	GoogleEvent event = todayEvents.get(i);
                
                		if(event.notifications != null || event.notifications.isEmpty()){
            
                			for(int j= 0; j<event.notifications.size(); j++){
           
                			Date date = event.notifications.get(j).getTime();
                			reminders.add(date);
                			}
                		} 
    			}
    		}
			return reminders;	
    	}


	
}
