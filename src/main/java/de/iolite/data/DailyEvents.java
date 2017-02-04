package de.iolite.data;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;



import de.iolite.apps.example.devices.SonosController;

/** 
 * This class stores all the DAILY events and their attributes.
 * @author Ariane Ziehn
 * 
 */
public class DailyEvents {

	List<GoogleEvent> todayEvents = new LinkedList<GoogleEvent>();
	SonosController controller;
	

	public DailyEvents(List<GoogleEvent> list, final SonosController sonosController) {
		this.todayEvents = list;
		this.controller = sonosController;
		System.out.println(this.toString());
		setAlarm();
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
						
//						+ "<br/> Location: "
						+ replaceUmlaute(shortlocation)

						+
						
						// "\n Status:"+ dummy.Status +
//						"<br/> type: "
//						+ dummy.Color
//						+
						
						// "\n Start: "+dummy.begin.get(Calendar.HOUR_OF_DAY)+start+
						// "\n End: "+dummy.end.get(Calendar.HOUR_OF_DAY)+ende+"\n"
						// + newline;

						"<br/> " + dummy.begin.get(Calendar.HOUR_OF_DAY) + start
						+ " - " + dummy.end.get(Calendar.HOUR_OF_DAY) + ende;
				
				
//						if(dummy.attendee == null){
//							
//						}
//						else{ //(dummy.attendee != null || dummy.attendee.isEmpty()){
//						
//						//TODO Design... raus nehmen aus String wenn niemand anders dabei?
//						event += "<br/> attendee: ";
//								for(int k= 0; k<dummy.attendee.size(); k++){	
//						event += dummy.attendee.get(k)+",";
//						}
//						}
//						
						
//						if(dummy.notifications != null || dummy.notifications.isEmpty()){
//							
//							//TODO Design... raus nehmen aus String wenn niemand anders dabei?
//							for(int j= 0; j<dummy.notifications.size(); j++){
//							event += "<br/> notification: "
//							+ dummy.notifications.get(j).getTime();
//							}
//						}
						
						event += "<br/>" + "<br/>";
						
						
//						sonosController.notifySonos(dummy.notifications.get(0).getTime());

			}
			return event;
		} else {
			return event + "today no upcoming events";
		}
	}

	public void setAlarm (){
    	
    		if (!todayEvents.isEmpty()) {
            
    			for (int i = 0; i < todayEvents.size(); i++) {
               	 	GoogleEvent event = todayEvents.get(i);
                
                		if(event.notifications != null || event.notifications.isEmpty()){
            
                			for(int j= 0; j<event.notifications.size(); j++){
           
                			Date date = event.notifications.get(j).getTime();
                			controller.playSongAt(date);
                			}
                		} 
    			}
    		}	
    	}


	
}
