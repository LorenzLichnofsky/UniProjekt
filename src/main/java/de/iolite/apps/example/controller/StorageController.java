package de.iolite.apps.example.controller;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;

import de.iolite.app.api.environment.EnvironmentAPI;
import de.iolite.app.api.storage.StorageAPI;
import de.iolite.app.api.storage.StorageAPIException;
import de.iolite.apps.example.CalendarIntegrationAppMain;

public class StorageController {

	@Nonnull
	private final StorageAPI api;
	
	/**
	 * Constructor with the storageAPI that is initialized in the startHook() Method of the Main Class.
	 *
	 * @param storageAPI
	 * @see CalendarIntegrationAppMain
	 */
	public StorageController(@Nonnull final StorageAPI storageAPI) {
		this.api = Validate.notNull(storageAPI, "'environmentAPI' must not be null");
	}
	
	public boolean isSonosEnabled(){
		
		try {
			
			return this.api.loadString("Sonos").equals("true");
		
		} catch (StorageAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return false;
		
	}
	
	public String getURI(){
		
		String URI = null; 
		
		try {
			URI = this.api.loadString("SonosURI");
		} catch (StorageAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return URI;
	}
}
