package de.iolite.apps.example.controller;

import de.iolite.app.api.environment.EnvironmentAPI;
import de.iolite.apps.example.CalendarIntegrationAppMain;

/** Handles the Current Situation of the user. */
public class EnvironmentController {
	
	EnvironmentAPI environmentAPI;

	/** 
	 * Constructor with the environmentAPI that is initialized in the startHook() Method of the Main Class.
	 *
	 * @param environmentAPI
	 * @see CalendarIntegrationAppMain
	 */
	public EnvironmentController (EnvironmentAPI environmentAPI){
		this.environmentAPI = environmentAPI;
	}

	/**
	 * Checks, whether the user is at home or not.
	 *
	 * @return Boolean value of the users current situation.
	 */
	public boolean isUserAtHome() {
		return this.environmentAPI.getCurrentSituationIdentifier().equals("https://dev.iolite.de/situation/type#AtHome");
	}
}
