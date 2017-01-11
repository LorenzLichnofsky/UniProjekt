package de.iolite.data;

import java.util.Calendar;
import java.util.List;

/**
 * by Ariane 
 * This class contains the attributes for one edit Google Calendar Event 
 */

public class GoogleEvent {
	String Name;
	String Color;
	String Location;
	Calendar begin;
	Calendar end;
	List<String> attendee; 
	List<java.util.Calendar> notifications;	


	//GETTER & SETTER 
	public String getColor() {
		return Color;
	}


	public void setColor(String color) {
		Color = color;
	}
	
	public List<java.util.Calendar> getNotifications() {
		return notifications;
	}


	public void setNotifications(List<java.util.Calendar> notifications) {
		this.notifications = notifications;
	}
		
	public List<String> getAttendee() {
		return attendee;
	}


	public void setAttendee(List<String> attendee) {
		this.attendee = attendee;
	}


	public String getName() {
		return Name;
	}


	public void setName(String name) {
		this.Name = name;
	}


	public String getLocation() {
		return Location;
	}


	public void setLocation(String location) {
		Location = location;
	}


	public Calendar getBegin() {
		return begin;
	}


	public void setBegin(Calendar begin) {
		this.begin = begin;
	}


	public Calendar getEnd() {
		return end;
	}


	public void setEnd(Calendar end) {
		this.end = end;
	}
	

}
