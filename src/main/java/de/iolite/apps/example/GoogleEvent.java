package de.iolite.apps.example;

import com.google.api.client.util.DateTime;

public class GoogleEvent {
	String Name;
	String Kind;
	String Status;
	public String getStatus() {
		return Status;
	}


	public void setStatus(String status) {
		Status = status;
	}


	String Location;
	DateTime begin;
	DateTime end;
	public String getKind() {
		return Kind;
	}


	public void setKind(String kind) {
		Kind = kind;
	}


	public String getName() {
		return Name;
	}


	public void setName(String name) {
		Name = name;
	}


	public String getLocation() {
		return Location;
	}


	public void setLocation(String location) {
		Location = location;
	}


	public DateTime getBegin() {
		return begin;
	}


	public void setBegin(DateTime begin) {
		this.begin = begin;
	}


	public DateTime getEnd() {
		return end;
	}


	public void setEnd(DateTime end) {
		this.end = end;
	}
	
	@Override
	public String toString(){
		String event = this.Name+", "+this.Kind+", "+this.Location+", "+this.Status;
		
		return event;
	}


	public static void main(String[] args) {
		

	}

	

}
