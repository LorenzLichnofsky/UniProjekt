package de.iolite.apps.example;

import java.util.List;

import de.iolite.app.api.device.DeviceAPIException;
import de.iolite.app.api.device.access.DeviceStringProperty;

public class ScrollingPublisher {
	
	/**
	 * Takes a DeviceStringProperty and a list of messages. It requests DeviceStringProperty a value update for each of the
	 * given messages, waiting 500 milliseconds each time
	 */
	public void pushMessages(DeviceStringProperty displayProperty, List<String> messages) {
		try {
			for (String message : messages) {
			displayProperty.requestValueUpdate(message);
			Thread.sleep(500);
			}
		} catch (DeviceAPIException | InterruptedException e) {
			throw new RuntimeException("Something failed, couldn't update value", e);
		}		
	}	

}
