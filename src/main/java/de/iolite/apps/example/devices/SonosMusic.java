package de.iolite.apps.example.devices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.DeviceAPIException;
import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceBooleanProperty;
import de.iolite.app.api.device.access.DeviceBooleanProperty.DeviceBooleanPropertyObserver;
import de.iolite.app.api.device.access.DeviceProperty;
import de.iolite.app.api.device.access.DeviceStringProperty;
import de.iolite.app.api.device.access.DeviceStringProperty.DeviceStringPropertyObserver;
import de.iolite.drivers.basic.DriverConstants;
import de.iolite.drivers.basic.DriverConstants.PlaybackState;

public class SonosMusic {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SonosMusic.class);
	
//	public void turnSonosOn (final Device device){
//
//		final DeviceBooleanProperty onProperty = device.getBooleanProperty(DriverConstants.PROFILE_PROPERTY_MediaPlayerDevice_on_ID);
//		
//		if (onProperty != null){
//			final boolean isDeviceOn = onProperty.getValue();
//			
//			if (isDeviceOn == false){
//				
//				try {
//					onProperty.requestValueUpdate(true);
//					onProperty.setObserver(new DeviceBooleanPropertyObserver() {
//
//						@Override
//						public void valueChanged(Boolean value) {
//							
//							if (value) {
//								LOGGER.info("Sonos turned on.");
//								//playMusic(device);
//							}
//							else {
//								LOGGER.info("Sonos still off.");
//							}
//							
//						}
//
//						@Override
//						public void deviceChanged(Device device) {
//							// TODO Auto-generated method stub
//							
//						}
//
//						@Override
//						public void keyChanged(String key) {
//							// TODO Auto-generated method stub
//							
//						}
//						
//					});
//					
//				} catch (DeviceAPIException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			} else {
//				LOGGER.info("Sonos is already on.");
//			}
//			
//		} else {
//			LOGGER.info("Property not found!");
//		}
//		
//		
//	}
	
	public void playMusic (final Device device){
		
//		for (DeviceProperty property: device.getProperties()){
//			LOGGER.debug("{}", property.getKey());
//		}
//		
		String PLAY = "play";
		
		final DeviceStringProperty playMusicProperty = device.getStringProperty(DriverConstants.PROFILE_PROPERTY_MediaPlayerDevice_playbackState_ID);

		
		if (playMusicProperty != null){
			final String musicStatus = playMusicProperty.getValue();
			LOGGER.debug("Der Status ist: '{}'", musicStatus);
			
			if (musicStatus != "play"){
				try {
					playMusicProperty.requestValueUpdateFromString(PLAY);
					playMusicProperty.setObserver(new DeviceStringPropertyObserver() {

						@Override
						public void deviceChanged(Device device) {
							// TODO Auto-generated method stub
						}

						@Override
						public void keyChanged(String key) {
							// TODO Auto-generated method stub
						}

						@Override
						public void valueChanged(String status) {
							LOGGER.debug("Sonos turned status to {}.", musicStatus);
						}
						
					});
					
				} catch (DeviceAPIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				LOGGER.debug("Current Status is already play.");
			}
			
		} else {
			LOGGER.info("PlayMusicProperty not found!");
		}
	}
	
	public void addSong (final Device device){
		
		 final DeviceStringProperty song = device.getStringProperty(DriverConstants.PROFILE_PROPERTY_MediaPlayerDevice_mediaURI_ID);
		 
		 if (song != null){
			 
			 try {
				song.requestValueUpdate("http://downloads.hendrik-motza.de/river.mp3");
				song.setObserver(new DeviceStringPropertyObserver(){

					@Override
					public void deviceChanged(Device device) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void keyChanged(String key) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void valueChanged(String value) {
						LOGGER.info("Habe die URI geändert in: {}", value);
						playMusic(device);
						
					}
					
				});
			} catch (DeviceAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		 
		 }
	}
}

