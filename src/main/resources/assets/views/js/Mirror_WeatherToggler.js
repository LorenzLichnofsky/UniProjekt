function saveMirror_WeatherEnabler(){
	  
	  console.log("Current Mirror_Weather Status:" + " " + $('#mirror-1').is(':checked'));

		    ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
		        success: function(storageAPI) {
		          storageAPI.action({
		            request: new ActionRequest(null, null, ".", "saveString", [new ValueParameter("Mirror_Weather"), new ValueParameter($('#mirror-1').is(':checked'))]),
		            success: function() {
		              console.log('Successfully executed requestValueUpdate for weather toggler');
		            },
		            error: function(storageAPI, responseRequestID, responseErrorCode, responseError) {
		              console.log("Requesting value update of ", device, " failed due to ",
		                responseErrorCode, ": ", responseError);
		            }
		          });
		         }
		        });  
}


function loadMirror_WeatherEnabler(){
	
	  ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
	      success: function(storageAPI) {
	        console.log("Test: get success for function 1 of mirror_weather toggle");
	        storageAPI.action({
	          request: new ActionRequest(null, null, ".", "loadString", [new ValueParameter("Mirror_Weather")]),
	          success: function(weather_status) {
	            console.log('Mirror_Weather: '+ weather_status);
	            var weather_status_as_bool = (weather_status === 'true');
	              $(document).ready(function() {
	                $('#mirror-1').prop('checked', weather_status_as_bool);
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