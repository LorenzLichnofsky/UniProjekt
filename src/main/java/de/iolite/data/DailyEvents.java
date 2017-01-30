package de.iolite.data;

import java.util.Calendar;
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
	SonosController sonosController;
	

	public DailyEvents(List<GoogleEvent> list) {
		this.todayEvents = list;
		System.out.println(this.toString());
	}

	public DailyEvents() {

	}

	public List<GoogleEvent> getTodayEvents() {
		return todayEvents;
	}

	public void setTodayEvents(List<GoogleEvent> todayEvents) {
		this.todayEvents = todayEvents;
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
				String shortlocation = dummy.Location.split(",")[0];
				if (minutes < 10) {
					ende = ":0" + minutes;
				} else
					ende = ":" + minutes;

				if (minutesStart < 10) {
					start = ":0" + minutesStart;
				} else
					start = ":" + minutesStart;

				event += dummy.Name + "<br/>"
//						+ "<br/> Location: "
						+ shortlocation
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


	
}
