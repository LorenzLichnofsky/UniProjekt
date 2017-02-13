
package de.iolite.apps.example.controller;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.environment.EnvironmentAPI;
import de.iolite.apps.example.CalendarIntegrationAppMain;
import de.iolite.apps.example.devices.SonosController;

/**
 * Class that handles environment queries.
 * @author Alia Siemund
 *
 */
public class EnvironmentController {

	/**
	 * @value - Type "at Home" in Iolite.
	 */
	@Nonnull
	private static final String SITUATION_TYPE_AT_HOME = "https://dev.iolite.de/situation/profile#Home";

	@Nonnull
	private final EnvironmentAPI api;
	
	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentController.class);

	/**
	 * Constructor with the environmentAPI that is initialized in the startHook() Method of the Main Class.
	 *
	 * @param environmentAPI
	 * @see CalendarIntegrationAppMain
	 */
	public EnvironmentController(@Nonnull final EnvironmentAPI environmentAPI) {
		this.api = Validate.notNull(environmentAPI, "'environmentAPI' must not be null");
	}

	/**
	 * Checks, whether the user is at home or not.
	 *
	 * @return boolean - value of the users current situation.
	 */
	public boolean isUserAtHome() {
		LOGGER.debug("Current Situation is: '{}'", this.api.getSituationProfileIdentifier());
		return SITUATION_TYPE_AT_HOME.equals(this.api.getSituationProfileIdentifier());
	}
}
