
package de.iolite.apps.example.devices;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.iolite.app.api.device.access.Device;
import de.iolite.apps.example.controller.EnvironmentController;
import de.iolite.utilities.concurrency.scheduler.Scheduler;

public class SonosController {

	Device sonos;
	Scheduler scheduler;
	EnvironmentController environment;

	public void setSonos(final Device sonos, final Scheduler scheduler) {
		this.sonos = sonos;
		this.scheduler = scheduler;
	}

	public void setTimer(final Date date) {
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
