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

/**
 * This class hosts all the methods, that are executing tasks on the Sonos Box in case of an event reminder.
 * @author Alia Siemund
 * @method setMediaURI
 * @method setValue
 * @method playSong
 * @method playMusic
 * @class StopPlayback
 */
public class SonosMusic {

	/**
	 * Is executed after Song plays 30sec on the Sonos Box. Stops Music immediately.
	 * @author Alia Siemund
	 * @method StopPlayback
	 */
	private static final class StopPlayback implements Runnable {

		@Nonnull
		private final DeviceStringProperty playback;

		private StopPlayback(@Nonnull final DeviceStringProperty playbackProperty) {
			this.playback = playbackProperty;
		}

		/**
		 * Scheduler Task that tries to change the Playbackstate of Sonos Box to stop after music played for 30sec by calling setValue() method.
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

	/**
	 * Method to set or change the Media URI of the Sonos Box. Requests actual saved URI from StorageController class.
	 * @param device
	 * @param storageController
	 * @return boolean
	 */
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

	/**
	 * Handles RequestValueUpdate method call of Iolite to change the Properties of the Sonos Device. Checks if device already has property value.
	 * @param property
	 * @param newValue
	 * @throws DeviceAPIException
	 */
	private static void setValue(@Nonnull final DeviceStringProperty property, @Nonnull final String newValue)
			throws DeviceAPIException {
		
		if (newValue.equals(property.getValue())) {
			LOGGER.debug("Property '{}' already has value '{}', skipping requestValueUpdate call", property.getKey(), newValue);
			return;
		}
		property.requestValueUpdate(newValue);
		LOGGER.trace("Requested value update in property '{}' for new value '{}'", property.getKey(), newValue);
	}

	/**
	 * Method that is called from SonosController class to execute the reminder task. Calls setMediaURI() and playMusic() method. Checks for success.
	 * @param device
	 * @param scheduler
	 * @param storageController
	 */
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

	/**
	 * Method that that tries to change the Playbackstate of Sonos Box to play by calling the setValue() method.
	 * @param device
	 * @param scheduler
	 * @return boolean
	 */
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