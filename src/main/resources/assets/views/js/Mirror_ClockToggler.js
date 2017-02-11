function saveMirror_ClockEnabler(){
	  
	  console.log("Current Mirror_Clock Status:" + " " + $('#mirror-2').is(':checked'));

		    ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
		        success: function(storageAPI) {
		          storageAPI.action({
		            request: new ActionRequest(null, null, ".", "saveString", [new ValueParameter("Mirror_Clock"), new ValueParameter($('#mirror-2').is(':checked'))]),
		            success: function() {
		              console.log('Successfully executed requestValueUpdate for mirror toggler');
		            },
		            error: function(storageAPI, responseRequestID, responseErrorCode, responseError) {
		              console.log("Requesting value update of ", device, " failed due to ",
		                responseErrorCode, ": ", responseError);
		            }
		          });
		         }
		        });  
}


function loadMirror_ClockEnabler(){
	
	  ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
	      success: function(storageAPI) {
	        console.log("Test: get success for function 1 of mirror_clock toggle");
	        storageAPI.action({
	          request: new ActionRequest(null, null, ".", "loadString", [new ValueParameter("Mirror_Clock")]),
	          success: function(clock_status) {
	            console.log('Mirror_Clock: '+ clock_status);
	            var clock_status_as_bool = (clock_status === 'true');
	              $(document).ready(function() {
	                $('#mirror-2').prop('checked', clock_status_as_bool);
	                
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