package de.iolite.data;

import java.util.Calendar;
import java.util.List;

/**
 * This class contains the attributes for one edit Google Calendar Event.
 * @author Ariane Ziehn
 * @version 1.0
 * 
 */
public class GoogleEvent {
	String Name;
	String Color;
	String Location;
	Calendar begin;
	Calendar end;
	List<String> attendee; 
	List<java.util.Calendar> notifications;	


	/**
	 * Gets the color of a specific event.
	 * @return String Color attribute of the event
	 */
	public String getColor() {
		return Color;
	}

	/**
	 * Sets the color attribute of a specific event.
	 * @param color 
	 */
	public void setColor(String color) {
		Color = color;
	}
	
	/**
	 * Gets notifications of the specific event from a List.
	 * @return List of notifications
	 */
	public List<java.util.Calendar> getNotifications() {
		return notifications;
	}

	/**
	 * Sets notifications of the event into a List.
	 * @param notifications
	 */
	public void setNotifications(List<java.util.Calendar> notifications) {
		this.notifications = notifications;
	}
	
	/**
	 * Gets Attendees of the specific event from a List.
	 * @return List of attendees
	 */
	public List<String> getAttendee() {
		return attendee;
	}

	/**
	 * Sets attendees of the event into a List.
	 * @param attendee
	 */
	public void setAttendee(List<String> attendee) {
		this.attendee = attendee;
	}

	/**
	 * Gets the name of a specific event.
	 * @return String Name of the event
	 */
	public String getName() {
		return Name;
	}

	/**
	 * Sets the name of a specific event.
	 * @param name
	 */
	public void setName(String name) {
		this.Name = name;
	}

	/**
	 * Gets the location of a specific event.
	 * @return String Location where the event takes place
	 */
	public String getLocation() {
		return Location;
	}

	/**
	 * Sets the location where the specific event takes place.
	 * @param location
	 */
	public void setLocation(String location) {
		Location = location;
	}

	/**
	 * Gets the time where the specific event starts.
	 * @return Calendar begin
	 */
	public Calendar getBegin() {
		return begin;
	}

	/**
	 * Sets the start time of a specific event
	 * @param begin 
	 */
	public void setBegin(Calendar begin) {
		this.begin = begin;
	}

	/**
	 * Gets the end time of a specific event.
	 */
	public Calendar getEnd() {
		return end;
	}

	/**
	 * Sets the end time of a specific event.
	 * @param end
	 */
	public void setEnd(Calendar end) {
		this.end = end;
	}
	

}
