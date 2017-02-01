
package de.iolite.apps.example.devices;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.DeviceAPIException;
import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceStringProperty;
import de.iolite.drivers.basic.DriverConstants;
import de.iolite.utilities.concurrency.scheduler.Scheduler;

public class SonosMusic {

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(SonosMusic.class);

	public void addSong(@Nonnull final Device device, @Nonnull final Scheduler scheduler) {
		Validate.notNull(device, "'device' must not be null");
		Validate.notNull(scheduler, "'scheduler' must not be null");
		if (!setMediaURI(device, "http://downloads.hendrik-motza.de/river.mp3")) {
			LOGGER.error("Failed to set song URI, aborting");
			return;
		}
		playMusic(device, scheduler);
	}

	private static boolean setMediaURI(@Nonnull final Device device, @Nonnull final String value) {
		final DeviceStringProperty mediaURI = device.getStringProperty(DriverConstants.PROFILE_PROPERTY_MediaPlayerDevice_mediaURI_ID);
		if (mediaURI == null) {
			LOGGER.warn("Device '{}' has no '{}' property, failed to set URI", device.getIdentifier(),
					DriverConstants.PROFILE_PROPERTY_MediaPlayerDevice_mediaURI_ID);
			return false;
		}
		try {
			mediaURI.requestValueUpdate(value);
			LOGGER.debug("Successfully set URI '{}' in device '{}'", value, device.getIdentifier());
			return true;
		}
		catch (final DeviceAPIException e) {
			LOGGER.error("Failed to set new URI '{}' in device '{}' due to error: {}", value, device.getIdentifier(), e);
			return false;
		}
	}

	private void playMusic(@Nonnull final Device device, @Nonnull final Scheduler scheduler) {
		final String PLAY = "play";
		final DeviceStringProperty playMusicProperty = device.getStringProperty(DriverConstants.PROFILE_PROPERTY_MediaPlayerDevice_playbackState_ID);
		if (playMusicProperty != null) {
			final String musicStatus = playMusicProperty.getValue();
			if (musicStatus != "play") {
				try {
					playMusicProperty.requestValueUpdateFromString(PLAY);
				}
				catch (final DeviceAPIException e) {
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

	private Runnable stopMusic(@Nonnull final Device device) {
		Validate.notNull(device, "'device' must not be null");
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
