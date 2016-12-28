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
	

}
