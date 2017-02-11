function saveSonosEnabler(){
	  
	  console.log("Current Sonos Status:" + " " + $('#sonos_checker').is(':checked'));

		    ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
		        success: function(storageAPI) {
		          storageAPI.action({
		            request: new ActionRequest(null, null, ".", "saveString", [new ValueParameter("Sonos"), new ValueParameter($('#sonos_checker').is(':checked'))]),
		            success: function() {
		              console.log('Successfully executed requestValueUpdate for sonos toggler');
		            },
		            error: function(storageAPI, responseRequestID, responseErrorCode, responseError) {
		              console.log("Requesting value update of ", device, " failed due to ",
		                responseErrorCode, ": ", responseError);
		            }
		          });
		         }
		        });  
}


function loadSonosEnabler(){
	
	  ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
	      success: function(storageAPI) {
	        console.log("Test: get success for function 1 of mirror toggle");
	        storageAPI.action({
	          request: new ActionRequest(null, null, ".", "loadString", [new ValueParameter("Sonos")]),
	          success: function(sonos_status) {
	            console.log('Sonos: '+ sonos_status);
	            var sonos_status_as_bool = (sonos_status === 'true');
	              $(document).ready(function() {
	                $('#sonos_checker').prop('checked', sonos_status_as_bool);
	                
	                if (sonos_status_as_bool) {
		   		    	 $(".sonostext").show();
		   		    	 $( "#sonos_triangle" ).prop( "checked", true );
		                }
	                
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
