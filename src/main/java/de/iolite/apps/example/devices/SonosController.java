
package de.iolite.apps.example.devices;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import de.iolite.app.api.device.access.Device;
import de.iolite.apps.example.controller.EnvironmentController;
import de.iolite.utilities.concurrency.scheduler.Scheduler;

public class SonosController {

	@Nullable
	private Device sonos;

	@Nullable
	private Scheduler scheduler;

	@Nullable
	private EnvironmentController environment;

	public void setSonos(@Nonnull final Device sonos, @Nonnull final Scheduler scheduler) {
		this.sonos = Validate.notNull(sonos, "'sonos' must not be null");
		this.scheduler = Validate.notNull(scheduler, "'scheduler' must not be null");
	}

	public void setTimer(@Nonnull final Date date) {
		Validate.notNull(date, "'date' must not be null");
		final long millisToEvent = date.getTime() - System.currentTimeMillis();
		if (millisToEvent < 0) {
			throw new IllegalArgumentException(String.format("Cannot schedule timer for past date '%s'", date));
		}
		this.scheduler.schedule(this::addSong, millisToEvent, TimeUnit.MILLISECONDS);
	}

	private void addSong() {
		if (this.environment.isUserAtHome() == true) {
			new SonosMusic().addSong(this.sonos, this.scheduler);
		}
	}
}
