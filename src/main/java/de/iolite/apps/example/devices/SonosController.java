
package de.iolite.apps.example.devices;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.access.Device;
import de.iolite.apps.example.controller.EnvironmentController;
import de.iolite.utilities.concurrency.scheduler.Scheduler;

public class SonosController {

	private static final class Configured implements State {

		@Nonnull
		private final Device sonosDevice;

		@Nonnull
		private final Scheduler taskScheduler;

		@Nonnull
		private final EnvironmentController environment;

		private Configured(@Nonnull final Device sonos, @Nonnull final Scheduler scheduler, @Nonnull final EnvironmentController environmentController) {
			this.sonosDevice = sonos;
			this.taskScheduler = scheduler;
			this.environment = environmentController;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setSonos(@Nonnull final SonosController context, @Nonnull final Device sonos, @Nonnull final Scheduler scheduler,
				@Nonnull final EnvironmentController environmentController) {
			context.setState(new Configured(sonos, scheduler, environmentController));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void playSongAt(@Nonnull final SonosController context, @Nonnull final Date date) {
			final long millisToEvent = date.getTime() - System.currentTimeMillis();
			if (millisToEvent < 0) {
				throw new IllegalArgumentException(String.format("Cannot schedule timer for past date '%s'", date));
			}
			this.taskScheduler.schedule(this::addSong, millisToEvent, TimeUnit.MILLISECONDS);
			LOGGER.debug("Scheduled SONOS 'addSong' task in {}s", TimeUnit.MILLISECONDS.toSeconds(millisToEvent));
		}

		private void addSong() {
			if (this.environment.isUserAtHome()) {
				LOGGER.debug("User is at home, adding song to SONOS");
				new SonosMusic().addSong(this.sonosDevice, this.taskScheduler);
			}
			else {
				LOGGER.debug("User is not at home, SONOS song will not be added");
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return String.format("%s[device='%s']", getClass().getSimpleName(), this.sonosDevice.getIdentifier());
		}
	}

	private enum NotConfigured implements State {
		INSTANCE;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setSonos(@Nonnull final SonosController context, @Nonnull final Device sonos, @Nonnull final Scheduler scheduler,
				@Nonnull final EnvironmentController environmentController) {
			context.setState(new Configured(sonos, scheduler, environmentController));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void playSongAt(@Nonnull final SonosController context, @Nonnull final Date date) {
			throw new IllegalStateException("Not configured");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return getClass().getSimpleName();
		}
	}

	private interface State {

		void setSonos(@Nonnull SonosController context, @Nonnull final Device sonos, @Nonnull final Scheduler scheduler,
				@Nonnull final EnvironmentController environmentController);

		void playSongAt(@Nonnull SonosController context, @Nonnull final Date date);
	}

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(SonosController.class);

	@Nonnull
	private volatile State state = NotConfigured.INSTANCE;

	public void setSonos(@Nonnull final Device sonos, @Nonnull final Scheduler scheduler, @Nonnull final EnvironmentController environmentController) {
		Validate.notNull(sonos, "'sonos' must not be null");
		Validate.notNull(scheduler, "'scheduler' must not be null");
		Validate.notNull(environmentController, "'environmentController' must not be null");
		this.state.setSonos(this, sonos, scheduler, environmentController);
	}

	public void playSongAt(@Nonnull final Date date) {
		Validate.notNull(date, "'date' must not be null");
		this.state.playSongAt(this, date);
	}

	private void setState(@Nonnull final State newState) {
		this.state = newState;
		LOGGER.debug("Changed state in {}", toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%s[state='%s']", getClass().getSimpleName(), this.state);
	}
}
