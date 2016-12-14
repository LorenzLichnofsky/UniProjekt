package de.iolite.apps.example;

import java.util.Calendar;

/**
 * by Ariane 
 */






public class GoogleEvent {
	String Name;
	String Color;
	String Status;
	String Location;
	Calendar begin;
	Calendar end;
	
	//GETTER & SETTER 
	public String getColor() {
		return Color;
	}


	public void setColor(String color) {
		Color = color;
	}
	public String getStatus() {
		return Status;
	}


	public void setStatus(String status) {
		Status = status;
	}


	
//	public String getKind() {
//		return Kind;
//	}
//
//
//	public void setKind(String kind) {
//		Kind = kind;
//	}


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
	
	@Override
	public String toString(){
	
	int minutes = this.end.get(Calendar.MINUTE);
	int minutesStart = this.begin.get(Calendar.MINUTE);
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
	String event = " WAS? "+this.Name+"\n WO?: "+this.Location+
				"\n Status:"+ this.Status + 
				"\n FARBE: "+this.Color+
				"\n WANN? "+this.begin.get(Calendar.HOUR_OF_DAY)+start+
				"\n BIS "+this.end.get(Calendar.HOUR_OF_DAY)+ende+"\n";
		
		return event;
	}


	
	

}
