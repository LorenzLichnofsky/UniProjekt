function saveSportEnabler(){
	  
	  console.log("Sport Appointments selected:" + " " + $('#green').is(':checked'));

		    ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
		        success: function(storageAPI) {
		          storageAPI.action({
		            request: new ActionRequest(null, null, ".", "saveString", [new ValueParameter("Sport"), new ValueParameter($('#green').is(':checked'))]),
		            success: function() {
		              console.log('Successfully executed requestValueUpdate for sport appointments');
		            },
		            error: function(storageAPI, responseRequestID, responseErrorCode, responseError) {
		              console.log("Requesting value update of ", device, " failed due to ",
		                responseErrorCode, ": ", responseError);
		            }
		          });
		         }
		        });  
}


function loadSportEnabler(){
	
	  ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
	      success: function(storageAPI) {
	        console.log("Test: get success for function 1 of sport appointments");
	        storageAPI.action({
	          request: new ActionRequest(null, null, ".", "loadString", [new ValueParameter("Sport")]),
	          success: function(sport_status) {
	            console.log('Sport status: '+ sport_status);
	            var sport_status_as_bool = (sport_status === 'true');
	              $(document).ready(function() {
	                $('#green').prop('checked', sport_status_as_bool);
	                
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
