package de.iolite.apps.example.controller;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;

import de.iolite.app.api.storage.StorageAPI;
import de.iolite.app.api.storage.StorageAPIException;
import de.iolite.apps.example.CalendarIntegrationAppMain;

/**
 * Class that handles storage queries. 
 * @author Alia Siemund
 * @method isSonosEnabled()
 * @method getURI()
 */
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
	
	/**
	 * Boolean method that checks if Sonos Device is enabled from the user in the UI by checking the Storage value.
	 * @return boolean 
	 */
	public boolean isSonosEnabled(){
		
		try {
			
			return this.api.loadString("Sonos").equals("true");
		
		} catch (StorageAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return false;
		
	}
	
	/**
	 * Method that turns back the saved Song URI from the Storage.
	 * @return String - Song URI
	 */
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
