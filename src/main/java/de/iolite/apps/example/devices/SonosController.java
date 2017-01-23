package de.iolite.apps.example.devices;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.iolite.app.api.device.access.Device;

public class SonosController {
	
	static Device sonos = null;

	public void setSonos (Device device){
		sonos = device;
	}
	
	public void notifySonos(Date date){
		//hier müsste noch überprüft werden, ob Sonos überhaupt enabled ist. Oder es wird schon vorher überprüft!
		Date reminderTime = date;
		Timer timer = new Timer();
		timer.schedule(remindSonos(), reminderTime);
		
	}
	
	public TimerTask remindSonos(){
		new SonosMusic().addSong(sonos);
		return null;
	}
	

}
