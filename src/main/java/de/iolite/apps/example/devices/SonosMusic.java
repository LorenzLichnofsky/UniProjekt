
package de.iolite.apps.example.devices;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.DeviceAPIException;
import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceStringProperty;
import de.iolite.drivers.basic.DriverConstants;
import de.iolite.utilities.concurrency.scheduler.Scheduler;

public class SonosMusic {

	private static final Logger LOGGER = LoggerFactory.getLogger(SonosMusic.class);

	public void addSong(final Device device, final Scheduler scheduler) {

		final DeviceStringProperty song = device.getStringProperty(DriverConstants.PROFILE_PROPERTY_MediaPlayerDevice_mediaURI_ID);
		if (song != null) {
			try {
				song.requestValueUpdate("http://downloads.hendrik-motza.de/river.mp3");
			}
			catch (final DeviceAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			playMusic(device, scheduler);
		}
		else {
			LOGGER.info("Song Property not found!");
		}
	}

	public void playMusic(final Device device, final Scheduler scheduler) {
		final String PLAY = "play";
		final DeviceStringProperty playMusicProperty = device.getStringProperty(DriverConstants.PROFILE_PROPERTY_MediaPlayerDevice_playbackState_ID);
		if (playMusicProperty != null) {
			final String musicStatus = playMusicProperty.getValue();
			if (musicStatus != "play") {
				try {
					playMusicProperty.requestValueUpdateFromString(PLAY);
				}
				catch (final DeviceAPIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				final TimeUnit sec = TimeUnit.SECONDS;
				scheduler.schedule(stopMusic(device), 15, sec);
			}
			else {
				LOGGER.debug("Current Status is already play.");
			}
		}
		else {
			LOGGER.info("PlayMusicProperty not found!");
		}
	}

	public Runnable stopMusic(final Device device) {
		return new Runnable() {
			@Override
			public void run() {
				final String STOP = "stop";
				final DeviceStringProperty stopMusicProperty = device.getStringProperty(DriverConstants.PROFILE_PROPERTY_MediaPlayerDevice_playbackState_ID);
				if (stopMusicProperty != null) {
					final String musicStatus = stopMusicProperty.getValue();
					if (musicStatus != "stop") {
						try {
							stopMusicProperty.requestValueUpdateFromString(STOP);
						}
						catch (final DeviceAPIException e) {
							e.printStackTrace();
						}
					}
					else {
						LOGGER.debug("Current Status is already stop.");
					}
				}
				else {
					LOGGER.info("PlayMusicProperty not found!");
				}
			}
		};
	}
}
