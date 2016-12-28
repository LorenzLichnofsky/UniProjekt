package de.iolite.apps.example;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class DailyEvents {
	List<GoogleEvent> todayEvents = new LinkedList<GoogleEvent>();
	
	public DailyEvents(List<GoogleEvent> list){
		this.todayEvents = list;
		//TODO toString to mirror 
		System.out.println(this.toString());
	}
	
	
		
		@Override
		public String toString(){
		String event = "";	
		for(int i=0; i<todayEvents.size(); i++){
		GoogleEvent dummy = todayEvents.get(i);
		int minutes = dummy.end.get(Calendar.MINUTE);
		int minutesStart = dummy.begin.get(Calendar.MINUTE);
		String ende = "";
		String start = "";
		if (minutes < 10){
		ende = ":0"+minutes;
		}
		else
		ende = ":"+minutes;	
		if (minutesStart < 10){
		start = ":0"+minutes;
		}
		else
		start = ":"+minutes;	
		event += " WAS? "+dummy.Name+"\n WO?: "+dummy.Location+
					"\n Status:"+ dummy.Status + 
					"\n FARBE: "+dummy.Color+
					"\n WANN? "+dummy.begin.get(Calendar.HOUR_OF_DAY)+start+
					"\n BIS "+dummy.end.get(Calendar.HOUR_OF_DAY)+ende+"\n";
		}	
			return event;
		}


		
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
