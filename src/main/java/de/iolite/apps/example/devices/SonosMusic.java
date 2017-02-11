package de.iolite.apps.example.devices;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.DeviceAPIException;
import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceProperty;
import de.iolite.app.api.device.access.DeviceStringProperty;
import de.iolite.apps.example.controller.StorageController;
import de.iolite.drivers.basic.DriverConstants;
import de.iolite.utilities.concurrency.scheduler.Scheduler;

public class SonosMusic {

	private static final class StopPlayback implements Runnable {

		@Nonnull
		private final DeviceStringProperty playback;

		private StopPlayback(@Nonnull final DeviceStringProperty playbackProperty) {
			this.playback = playbackProperty;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			try {
				setValue(this.playback, "stop");
			}
			catch (final DeviceAPIException e) {
				LOGGER.error("Failed to stop playback in device '{}' due to error: {}", this.playback.getDevice(), e);
			}
		}
	}

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(SonosMusic.class);

	private static boolean setMediaURI(@Nonnull final Device device, @Nonnull final StorageController storageController) {
		
		final String URI = storageController.getURI();
		
		final DeviceStringProperty mediaURI = device.getStringProperty(DriverConstants.PROFILE_PROPERTY_MediaPlayerDevice_mediaURI_ID);

		if (mediaURI == null) {
			LOGGER.warn("Device '{}' has no '{}' property, failed to set URI", device.getIdentifier(),
					DriverConstants.PROFILE_PROPERTY_MediaPlayerDevice_mediaURI_ID);
			return false;
		}
		try {
			mediaURI.requestValueUpdate(URI);
			LOGGER.debug("Successfully set URI '{}' in device '{}'", URI, device.getIdentifier());
			return true;
		}
		catch (final DeviceAPIException e) {
			LOGGER.error("Failed to set new URI '{}' in device '{}' due to error: {}", URI, device.getIdentifier(), e);
			return false;
		}
	}

	private static void setValue(@Nonnull final DeviceStringProperty property, @Nonnull final String newValue)
			throws DeviceAPIException {
		if (newValue.equals(property.getValue())) {
			LOGGER.debug("Property '{}' already has value '{}', skipping requestValueUpdate call", property.getKey(), newValue);
			return;
		}
		property.requestValueUpdate(newValue);
		LOGGER.trace("Requested value update in property '{}' for new value '{}'", property.getKey(), newValue);
	}

	public static void playSong(@Nonnull final Device device, @Nonnull final Scheduler scheduler, @Nonnull final StorageController storageController) {
		Validate.notNull(device, "'device' must not be null");
		Validate.notNull(scheduler, "'scheduler' must not be null");
		Validate.notNull(storageController, "'storageController' must not be null");
		
		if (!setMediaURI(device, storageController)) {
			LOGGER.error("Failed to set song URI in device '{}', aborting", device.getIdentifier());
			return;
		}
		if (!playMusic(device, scheduler)) {
			LOGGER.error("Failed properly schedule music playing with device '{}'", device.getIdentifier());
		}
		LOGGER.debug("Successfully scheduled music playing with device '{}'", device.getIdentifier());
	}

	private static boolean playMusic(@Nonnull final Device device, @Nonnull final Scheduler scheduler) {
		final DeviceStringProperty playbackState = device.getStringProperty(DriverConstants.PROFILE_PROPERTY_MediaPlayerDevice_playbackState_ID);
		if (playbackState == null) {
			LOGGER.error("Device '{}' has no '{}' property, music will not be played", device.getIdentifier(),
					DriverConstants.PROFILE_PROPERTY_MediaPlayerDevice_playbackState_ID);
			return false;
		}

		try {
			setValue(playbackState, "play");
			LOGGER.debug("Requested change of playback state of device '{}' to play.", device.getIdentifier());
		}
		catch (final DeviceAPIException e) {
			LOGGER.error("Failed to change playback state of device '{}' to play due to error: {}", device.getIdentifier(), e);
			return false;
		}

		scheduler.schedule(new StopPlayback(playbackState), 30, TimeUnit.SECONDS);
		LOGGER.debug("Scheduled playback stop for device '{}'", device.getIdentifier());
		return true;
	}
}