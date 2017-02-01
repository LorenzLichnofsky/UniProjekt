
package de.iolite.apps.example.devices;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

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
		final Date reminderTime = date;
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (SonosController.this.environment.isUserAtHome() == true) {
					new SonosMusic().addSong(SonosController.this.sonos, SonosController.this.scheduler);
				}
			}
		}, reminderTime);
	}
}
