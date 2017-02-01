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


function loadMirror_CalendarEnabler(){
	
	  ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
	      success: function(storageAPI) {
	        console.log("Test: get success for function 1 of mirror_calendar toggle");
	        storageAPI.action({
	          request: new ActionRequest(null, null, ".", "loadString", [new ValueParameter("Mirror_Calendar")]),
	          success: function(calendar_status) {
	            console.log('Mirror_Calendar: '+ calendar_status);
	            var calendar_status_as_bool = (calendar_status === 'true');
	              $(document).ready(function() {
	                $('#mirror-4').prop('checked', calendar_status_as_bool);
	                
	              });
	          },
	          error: function(storageAPI, responseRequestID, responseErrorCode, responseError) {
	            console.log("Requesting value update of ", device, " failed due to ",
	              responseErrorCode, ": ", responseError);
	          }
	        });

	      }
	 });
	}