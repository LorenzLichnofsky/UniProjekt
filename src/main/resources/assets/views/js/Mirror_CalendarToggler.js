function saveMirror_CalendarEnabler(){
	  
	  console.log("Current Mirror_Calendar Status:" + " " + $('#mirror-4').is(':checked'));

		    ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
		        success: function(storageAPI) {
		          storageAPI.action({
		            request: new ActionRequest(null, null, ".", "saveString", [new ValueParameter("Mirror_Calendar"), new ValueParameter($('#mirror-4').is(':checked'))]),
		            success: function() {
		              console.log('Successfully executed requestValueUpdate for calendar toggler');		              
		            },
		            error: function(storageAPI, responseRequestID, responseErrorCode, responseError) {
		              console.log("Requesting value update of ", device, " failed due to ",
		                responseErrorCode, ": ", responseError);
		            }
		          });
		         }
		        });  
}
