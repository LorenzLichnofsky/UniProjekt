package de.iolite.apps.example;

import java.util.LinkedList;
import java.util.List;

public class DailyEvents {
	List<GoogleEvent> todayEvents = new LinkedList<GoogleEvent>();
	
	public DailyEvents(List<GoogleEvent> list){
		this.todayEvents = list;
		//TODO mirror 
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
