
package de.iolite.apps.example.devices;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.access.Device;
import de.iolite.apps.example.controller.EnvironmentController;
import de.iolite.apps.example.controller.StorageController;
import de.iolite.data.DailyEvents;
import de.iolite.data.GoogleEvent;
import de.iolite.utilities.concurrency.scheduler.Scheduler;

/**
 * This class handles the execution of Sonos reminder tasks in context of the configured state.
 * @author Alia Siemund
 *
 */
public class SonosController {

	/**
	 * This class hosts all of the needed parameters to controll Sonos Box. 
	 * @implements State - State of the parameters must be set once.
	 * @author Alia Siemund
	 *
	 */
	private static final class Configured implements State {

		@Nonnull
		private final Device sonosDevice;

		@Nonnull
		private final Scheduler taskScheduler;

		@Nonnull
		private final EnvironmentController environment;
		
		@Nonnull
		private final DailyEvents dailyEvents;
		
		@Nonnull 
		private final StorageController storageController;
		
		@Nonnull
		private ScheduledFuture<?> SonosScheduledFuture = null;
		
		private List<Date> newReminders = null;
		private List<Date> oldReminders = null;

		/**
		 * Constructor of the class Configured.
		 * @param sonos
		 * @param scheduler
		 * @param environmentController
		 * @param dailyEvents
		 * @param storageController
		 */
		private Configured(@Nonnull final Device sonos, @Nonnull final Scheduler scheduler, @Nonnull final EnvironmentController environmentController, @Nonnull final DailyEvents dailyEvents, @Nonnull final StorageController storageController) {
			this.sonosDevice = sonos;
			this.taskScheduler = scheduler;
			this.environment = environmentController;
			this.dailyEvents = dailyEvents;
			this.storageController = storageController;
		}

		/**
		 * Method to sets the State of all parameters in the SonosController class context. 
		 * @param context
		 * @param sonos
		 * @param scheduler
		 * @param environmentController
		 * @param dailyEvents
		 * @param storageController
		 */
		@Override
		public void setSonos(@Nonnull final SonosController context, @Nonnull final Device sonos, @Nonnull final Scheduler scheduler,
				@Nonnull final EnvironmentController environmentController, @Nonnull DailyEvents dailyEvents, @Nonnull final StorageController storageController) {
			context.setState(new Configured(sonos, scheduler, environmentController, dailyEvents, storageController));
		}

		/**
		 * Method that schedules reminder tasks from reminders in DailyEvents class.
		 * @param context
		 * @param dailyEvents
		 * @param storageController
		 * @see DailyEvents
		 */
		@Override
		public void playSongAt(@Nonnull final SonosController context, @Nonnull final DailyEvents dailyEvents, @Nonnull final StorageController storageController) {
		
			//Write reminders in a list of old reminders before getting the actual reminders
			if (newReminders != null){
				this.oldReminders = this.newReminders;
			}
		
		//Get reminders from dailyEvents class.
		this.newReminders = dailyEvents.getAlarm();
		
		//Check wheteher reminders are empty
			if (!newReminders.isEmpty()){	
				
					Date reminderTime = newReminders.get(0);
				
					final long millisToEvent = reminderTime.getTime() - System.currentTimeMillis();
					
					this.taskScheduler.schedule(() -> {
						
					this.addSong(reminderTime);}, millisToEvent, TimeUnit.MILLISECONDS);
					LOGGER.debug("Scheduled SONOS 'addSong' task in {}s", TimeUnit.MILLISECONDS.toSeconds(millisToEvent));
					
				
			} else {
				LOGGER.warn("No reminders. Sonos will not be scheduled!");
			}
		}

		/**
		 * Method that executes the scheduled reminder tasks in case the user is at home and enabled Sonos Box in the UI.
		 * @see StorageController
		 * @see EnvironmentController
		 */
		private void addSong(Date reminderTime) {
			
			//Checks whether user enabled Sonos Box in the UI
			if (this.storageController.isSonosEnabled()){
				
				//Checks whether the user is currently at home.
				if (this.environment.isUserAtHome()) {
					
					//Checks whether this task is valid or updated.
					if (isReminderUpToDate(reminderTime)){
						LOGGER.debug("User is at home, adding song to SONOS");
						SonosMusic.playSong(this.sonosDevice, this.taskScheduler, this.storageController);
					} else {
						//LOGGER.debug("This Reminder is not up to date. Task will not be scheduled!");
					}
				} else {
					LOGGER.debug("User is not at home, SONOS song will not be added.");
				}
				
			} else {
				LOGGER.warn("Sonos is not enabled. Song will not be added.");
			}
		}
		
		/**
		 * Checks whether the reminder is the first reminder in the list. If not, reminders are updated and the task is not up to date.
		 * @param reminderTime
		 * @return boolean
		 */
		private Boolean isReminderUpToDate(Date reminderTime){
			
			if (this.newReminders !=null && this.oldReminders !=null){
				
				Date actualReminder = this.newReminders.get(0);
				
				
				if (actualReminder.equals(reminderTime)){
					return true;
				}
				
			}
			return false;
		}

		@Override
		public String toString() {
			return String.format("%s[device='%s']", getClass().getSimpleName(), this.sonosDevice.getIdentifier());
		}
	}

	/**
	 * Enum that hosts all the methods that are executed in case of an actual not configured state instance.
	 * @author Alia Siemund
	 *
	 */
	private enum NotConfigured implements State {
		INSTANCE;

		/**
		 * Sets state of the actual not configured SonosController class.
		 * @param context
		 * @param sonos
		 * @param scheduler
		 * @param environmentController
		 * @param dailyEvents
		 * @param storageController
		 */
		@Override
		public void setSonos(@Nonnull final SonosController context, @Nonnull final Device sonos, @Nonnull final Scheduler scheduler,
				@Nonnull final EnvironmentController environmentController, @Nonnull DailyEvents dailyEvents, @Nonnull final StorageController storageController) {
			context.setState(new Configured(sonos, scheduler, environmentController, dailyEvents, storageController));
		}

		/**
		 * Method that intercepts the try of scheduling a reminder task when the state of SonosController class is not yet set.
		 * @throws IllegalStateException
		 */
		@Override
		public void playSongAt(@Nonnull final SonosController context, @Nonnull final DailyEvents dailyEvents, @Nonnull final StorageController storageController) {
			throw new IllegalStateException("Not configured");
		}

		@Override
		public String toString() {
			return getClass().getSimpleName();
		}
	}

	/**
	 * Interface that hosts the methods that are used in context of the state of this class.
	 * @author Alia Siemund
	 *
	 */
	private interface State {

		/**
		 * SetSonos method that is executed depending on the context state.
		 * @param context
		 * @param sonos
		 * @param scheduler
		 * @param environmentController
		 * @param dailyEvents
		 * @param storageController
		 */
		void setSonos(@Nonnull SonosController context, @Nonnull final Device sonos, @Nonnull final Scheduler scheduler,
				@Nonnull final EnvironmentController environmentController, @Nonnull final DailyEvents dailyEvents, @Nonnull final StorageController storageController);

		/**
		 * PlaySongAt method that is executed depending on the context state.
		 * @param context
		 * @param dailyEvents
		 * @param storageController
		 */
		void playSongAt(@Nonnull SonosController context, @Nonnull final DailyEvents dailyEvents, @Nonnull final StorageController storageController);
	}

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(SonosController.class);

	@Nonnull
	private volatile State state = NotConfigured.INSTANCE;

	/**
	 * Method that gets parameters from the initializeDeviceManager() method in Main class to set the state value of the interface.
	 * @param sonos
	 * @param scheduler
	 * @param environmentController
	 * @param dailyEvents
	 * @param storageController
	 * @see CalendarIntegrationAppMain
	 */
	public void setSonos(@Nonnull final Device sonos, @Nonnull final Scheduler scheduler, @Nonnull final EnvironmentController environmentController, @Nonnull final DailyEvents dailyEvents, @Nonnull final StorageController storageController) {
		Validate.notNull(sonos, "'sonos' must not be null");
		Validate.notNull(scheduler, "'scheduler' must not be null");
		Validate.notNull(environmentController, "'environmentController' must not be null");
		Validate.notNull(dailyEvents, "'dailyEvents' must not be null");
		Validate.notNull(storageController, "'storageController' must not be null");
		this.state.setSonos(this, sonos, scheduler, environmentController, dailyEvents, storageController);
	}

	/**
	 * Executes the playSongAt() method from the actual not configured state.
	 * @param dailyEvents
	 * @param storageController
	 */
	public void playSongAt(@Nonnull final DailyEvents dailyEvents, @Nonnull final StorageController storageController) {
		Validate.notNull(dailyEvents, "'dailyEvents' must not be null");
		Validate.notNull(storageController, "'storagecontroller' must not be null");
		this.state.playSongAt(this, dailyEvents, storageController);
	}

	/**
	 * Sets the 'state' value of SonosController class into newState that is defined in setSonos() methods.
	 * @value state
	 * @param newState
	 */
	private void setState(@Nonnull final State newState) {
		this.state = newState;
		LOGGER.debug("Changed state in {}", toString());
	}

	/**
	 * Formats the actual state of Sonos Controller into a loggable String.
	 */
	@Override
	public String toString() {
		return String.format("%s[state='%s']", getClass().getSimpleName(), this.state);
	}
}
